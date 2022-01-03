# Hilt Testing

## Unit tests

Unit tests do not require hilt to inject dependencies if construct injection is used. Dependencies can be passed directly into the class under test.

```kotlin
@ActivityScoped
class AnalyticsAdapter @Inject constructor(
  private val service: AnalyticsService
) { ... }

class AnalyticsAdapterTest {

  @Test
  fun `Happy path`() {
    // You don't need Hilt to create an instance of AnalyticsAdapter.
    // You can pass a fake or mock AnalyticsService.
    val adapter = AnalyticsAdapter(fakeAnalyticsService)
    assertEquals(...)
  }
}
```

## End-to-end tests

Hilt injects Dependencies in tests like in production.

## Adding testing dependencies

Add the following to your module's `build.gradle` file:

```gradle
// Hilt testing dependency
androidTestImplementation "com.google.dagger:hilt-android-testing:$version"
// make hilt generate code in the `androidTest` folder
kaptAndroidTest "com.google.dagger:hilt-android-compiler:$version"
```

> note: For `Robolectric` tests use `kaptTest` instead of `kaptAndroidTest`.

## Custom Test Runner

Hilt tests require a custom test runner that uses the An `application` that supports Hilt. `HiltTestApplication` is provided for use in tests.

## Set the test application in instrumented tests

To create one in `androidTest` folder create a file named `CustomTestRunner.kt` and add the following:

```kotlin
// A custom runner to set up the instrumented application class for tests.
class CustomTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
```

add the following in `app/build.gradle`:

```gradle
android {
  // Replace com.example.android.dagger with your class path.
  defaultConfig {
    instrumentationRunner = "com.example.android.hilt.CustomTestRunner"
  }
}
```

## Set the test application in Robolectric tests

You can set the `application` in the `robolectric.properties` file:

```properties
application = dagger.hilt.android.testing.HiltTestApplication
```

Alternatively, `@Config` annotation can be used to configure each test individually:

```kotlin
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class SettingsActivityTest {

  @get:Rule
  var hiltRule = HiltAndroidRule(this)

  // Robolectric tests here.
}
```

## Running Tests with Hilt

Hilt test need to be annotated with `@HiltAndroidTest` and `HiltAndroidRule` is used to perform injection for your tests.

```kotlin
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AppTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    ...
}
```

if you have multiple rules:

```kotlin
@HiltAndroidTest
class SettingsActivityTest {

  @get:Rule(order = 0)
  var HiltAndroidRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  var settingsActivityTestRule = SettingsActivityTestRule()

  // UI tests here.
}
```

## Injecting types in tests

`@Inject` can be used for field injection in tests. `hiltRule.inject()` populates the fields.

```kotlin
@HiltAndroidTest
class SettingsActivityTest {

  @get:Rule
  var hiltRule = HiltAndroidRule(this)

  @Inject
  lateinit var analyticsAdapter: AnalyticsAdapter

  @Before
  fun init() {
    hiltRule.inject()
  }

  @Test
  fun `happy path`() {
    // Can already use analyticsAdapter here.
  }
}
```

## Replacing a production binding

To fake or mock a instance of a dependency, a binding can be replaced by creating a test module using the `@TestInstallIn` annotation.

Create a test module:

```kotlin
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AnalyticsModule::class]
)
abstract class FakeAnalyticsModule {

  @Singleton
  @Binds
  abstract fun bindAnalyticsService(
    fakeAnalyticsService: FakeAnalyticsService
  ): AnalyticsService
}
```

## Replacing a binding in a single test

`@UninstallModules` annotation tells Hilt to ignore the production module. You must replace the module in the test class as well:

```kotlin
@UninstallModules(AnalyticsModule::class)
@HiltAndroidTest
class SettingsActivityTest {

  @Module
  @InstallIn(SingletonComponent::class)
  abstract class TestModule {

    @Singleton
    @Binds
    abstract fun bindAnalyticsService(
      fakeAnalyticsService: FakeAnalyticsService
    ): AnalyticsService
  }

  ...
}
```

## Binding new values

The `@BindValue` annotation allows you to bind fields in your tests.

```kotlin
@UninstallModules(AnalyticsModule::class)
@HiltAndroidTest
class SettingsActivityTest {

  @BindValue @JvmField
  val analyticsService: AnalyticsService = FakeAnalyticsService()

  ...
}
```

> `@UninstallModules` can impact test times, prefer using `@TestInstallIn`.

## launchFragmentInContainer

`launchFragmentInContainer` cannot be used with Hilt because it is not annotated with `@AndroidEntryPoint`.

Instead use:

HiltExt.kt

```kotlin
/**
 * launchFragmentInContainer from the androidx.fragment:fragment-testing library
 * is NOT possible to use right now as it uses a hardcoded Activity under the hood
 * (i.e. [EmptyFragmentActivity]) which is not annotated with @AndroidEntryPoint.
 *
 * As a workaround, use this function that is equivalent. It requires you to add
 * [HiltTestActivity] in the debug folder and include it in the debug AndroidManifest.xml file
 * as can be found in this project.
 */
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme,
    crossinline action: Fragment.() -> Unit = {}
) {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    ).putExtra(
        "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
        themeResId
    )

    ActivityScenario.launch<HiltTestActivity>(startActivityIntent).onActivity { activity ->
        val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        fragment.arguments = fragmentArgs
        activity.supportFragmentManager
            .beginTransaction()
            .add(android.R.id.content, fragment, "")
            .commitNow()

        fragment.action()
    }
}
```
