# AndroidX Test Libraries

AndroidX Test is a collect of Jetpack Libraries that help you test test Android applications. They are usually only need for Instrumented tests.

## add Gradle dependencies

Common AndroidX libraries are:

```gradle
dependencies {
    // Core library
    androidTestImplementation "androidx.test:core:$androidXTestVersion0"

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation "androidx.test:runner:$testRunnerVersion"
    androidTestImplementation "androidx.test:rules:$testRulesVersion"

    // Assertions
    androidTestImplementation "androidx.test.ext:junit:$testJunitVersion"
    androidTestImplementation "androidx.test.ext:truth:$truthVersion"

    // Espresso dependencies
    androidTestImplementation "androidx.test.espresso:espresso-core:$espressoVersion"
    androidTestImplementation "androidx.test.espresso:espresso-contrib:$espressoVersion"
    androidTestImplementation "androidx.test.espresso:espresso-intents:$espressoVersion"
    androidTestImplementation "androidx.test.espresso:espresso-accessibility:$espressoVersion"
    androidTestImplementation "androidx.test.espresso:espresso-web:$espressoVersion"
    androidTestImplementation "androidx.test.espresso.idling:idling-concurrent:$espressoVersion"

    // The following Espresso dependency can be either "implementation",
    // or "androidTestImplementation", depending on whether you want the
    // dependency to appear on your APK’"s compile classpath or the test APK
    // classpath.
    androidTestImplementation "androidx.test.espresso:espresso-idling-resource:$espressoVersion"
}
```

## Junit4 Rules

Junit rules to be used with the AndroidJunitRunner to reduce boilerplate for tests.

## AndroidJUnitRunner

Junit test runner that allows for instrumented JUnit 4 tests on Android. Required for Espresso, UI Automator, and Compose testing frameworks.

The test runner handles loading your tests and the app under test to a device, executing the tests, and reporting the test results.

Test runner supports the following tasks:

- Writing JUnit tests
- Accessing the app's context
- Filtering tests
- Sharing tests

`@RunWith(AndroidJunit4::class))`
: Used with any class that requires AndroidX Test.

```kotlin
@RunWith(AndroidJUnit4::class) // Only needed when mixing JUnit 3 and 4 tests
@LargeTest // Optional runner annotation
class ChangeTextBehaviorTest {
 val stringToBeTyped = "Espresso"
 // ActivityTestRule accesses context through the runner
 @get:Rule
 val activityRule = ActivityTestRule(MainActivity::class.java)

 @Test fun changeText_sameActivity() {
 // Type text and then press the button.
 onView(withId(R.id.editTextUserInput))
 .perform(typeText(stringToBeTyped), closeSoftKeyboard())
 onView(withId(R.id.changeTextBt)).perform(click())

 // Check that the text was changed.
 onView(withId(R.id.textToBeChanged))
 .check(matches(withText(stringToBeTyped)))
 }
}
```

## Accessing the Application's Context

The `ApplicationProvider.getApplicationContext()` method returns the application context.

The `InstrumentationRegistry` class provides access to low-level testing APIs. This class includes the `Instrumentation` object, the target app `Context` object, the test app Context object, and the command line arguments passed into your test.

## Filter Tests

The Test runner supports Android-specific annotations, including the following:

- _`@RequiresDevice`_
  : Test should only be run on an actual device.

- _`@SdkSuppress(minSdkVersion=23)`_
  : Suppressing running a testing on a lower Android API level then given.

- _`@SmallTest`, `@MediumTest`, and `@LargeTest`_
  : Classifies how long a test should take and how often it get should be run.

You can use size arguments by setting the following properties:

`android.testInstrumentationRunnerArguments.size=small`
`-Pandroid.testInstrumentationRunnerArguments.size=small`

## Shard tests

Allow grouping, _shards_, tests for parallelized exception across multiple servers.
