# Hilt

Hilt is a dependency injection library for Android that reduces boilerplate of doing manual dependency injection.

Hilt is based on top of Dagger.

Dagger provides:

- compile-time correctness
- run-time performance
- scalability
- Android studio support

## Adding dependencies

Add `hilt-android-gradle-plugin` to project `build.gradle` file.

```gradle
buildscript {
  ...
  dependencies {
    ...
    classpath 'com.google.dagger:hilt-android-gradle-plugin:2.38.1'
  }
}
```

Apply Gradle plugin to `app/build.gradle`.

```gradle
plugins {
  id 'kotlin-kapt'
  id 'dagger.hilt.android.plugin'
}
```

Add dependencies to `app/build.gradle`.

```gradle
dependencies {
  implementation "com.google.dagger:hilt-android:2.38.1"
  kapt "com.google.dagger:hilt-compiler:2.38.1"
}
```

Hilt uses Java 8, enable Java 8 in project. Add the following to the `app/build.gradle` file:

```gradle
android {
    ...
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

## Hilt application class

App must contain an `Application` class that is annotated with `@HiltAndroidApp`.

`@HiltAndroidApp` triggers Hilt's code generation and serves as the application's dependency container.

```kotlin
@HiltAndroidApp
class App: Application() { ... }
```

## Inject dependencies into Android classes

Hilt can provide dependencies Android classes that are annotated with `@AndroidEntryPoint` once Hilt is setup in your `Application` class.

Hilt supports:

- `Application` (via @HiltAndroidApp)
- `VieModel` (via @HiltViewModel)
- `Activity`
- `Fragment`
- `View`
- `Service`
- `BroadcastReceiver`

> Parents of Dependencies must be annotated as a Hilt class as well

```kotlin
@AndroidEntryPoint
class SomeActivity : AppCompatActivity() {
  @Inject
  lateinit var someDependency: SomeDependency // Field Injection
}
```

> Fields injected cannot be private

## Define Hilt bindings

A binding allows Hilt to provide an instance of a dependency

**Constructor Injection** allows Hilt to provide a dependency of a class. Parameters of an annotated constructor of a class are dependencies. Therefor, Hilt must also know how
to provide them.

At build time, Hilt generates Dagger components and:

- builds and validates a dependency graph, ensuring there are no unsatisfied dependencies and no dependency cycles
- Generates the classes used at runtime to provide the actual dependencies

## Hilt modules

Allows injection of Interfaces and classes you do not own.

A Hilt module is a class that is annotated with `@Module` and `@InstallIn`.

## Injecting Interfaces

`@Binds` annotation tells Hilt how to provide an instance of an interface:

- The function return type is the interface to provide.
- The function parameter is the implementation to provide.

```kotlin
interface SomeInterface {
  fun SomeMethod()
}

// Constructor-injection to tell Hilt how to provide dependency
class SomeInterfaceImpl @Inject contructor(
  ...
): SomeInterface { ... }

@Module
@InstallIn(ActivityComponent::class)
abstract class SomeModule {

  @Binds
  abstract fun bindSomeIntrerface(
    someInterfaceImpl: SomeInterfaceImpl
  ): SomeInterface
}
```

## Inject External Dependencies and builder pattern classes

`@Provides` tells hilt how to provide a class you do not own or must configure:

- The function return type is the type to provide
- The function parameters are the dependencies of the type
- The function body is how to create an instance of the type

```kotlin
@Module
@InstallIn(ActivityComponent::class)
object SomeModule {

  @Provides
  fun provideSomeExternalDependency(
    // Dependencies
  ): SomeExternalDependency {
    return Retrofit.Builder()
      .baseUrl("https://example.com")
      .build()
      .create(SomeExternalDependency::class.java)
  }
}
```

## Providing multiple bindings for the same type

**Qualifiers** allow for defining multiple bindings for the same type.

Creating the **qualifiers**:

```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OtherInterceptorOkHttpClient
```

Using **qualifiers**:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @AuthInterceptorOkHttpClient
  @Provides
  fun provideAuthInterceptorOkHttpClient(
    authInterceptor: AuthInterceptor
  ): OkHttpClient {
      return OkHttpClient.Builder()
               .addInterceptor(authInterceptor)
               .build()
  }

  @OtherInterceptorOkHttpClient
  @Provides
  fun provideOtherInterceptorOkHttpClient(
    otherInterceptor: OtherInterceptor
  ): OkHttpClient {
      return OkHttpClient.Builder()
               .addInterceptor(otherInterceptor)
               .build()
  }
}
```

```kotlin
// As a dependency of another class.
@Module
@InstallIn(ActivityComponent::class)
object AnalyticsModule {

  @Provides
  fun provideAnalyticsService(
    @AuthInterceptorOkHttpClient okHttpClient: OkHttpClient
  ): AnalyticsService {
      return Retrofit.Builder()
               .baseUrl("https://example.com")
               .client(okHttpClient)
               .build()
               .create(AnalyticsService::class.java)
  }
}

// As a dependency of a constructor-injected class.
class ExampleServiceImpl @Inject constructor(
  @AuthInterceptorOkHttpClient private val okHttpClient: OkHttpClient
) : ...

// At field injection.
@AndroidEntryPoint
class ExampleActivity: AppCompatActivity() {

  @AuthInterceptorOkHttpClient
  @Inject
  lateinit var okHttpClient: OkHttpClient
}
```

> If you provide a Qualifier then annotate all other providers of that type. Otherwise Hilt could inject the wrong dependency.

## Predefined qualifiers in Hilt

Hilt provides predefined qualifiers. Some examples are `@ActivityContext` and `@ApplicationContext`.

```kotlin
class SomeClass @Inject constructor(
  @ApplicationContext private val context: Context,
  private val anotherClass: AnotherClass
) { ... }
```

## InstallIn components for Android classes

Hilt provides the following components:

| Hilt component            | Injector for |
| ------------------------- | ------------ |
| SingletonComponent        | Application  |
| ActivityRetainedComponent | N/A          |
| ViewModelComponent        | ViewModel    |
| ActivityComponent         | Activity     |
| FragmentComponent         | Fragment     |
| ViewComponent             | View         |
| ViewWithFragmentComponent | View [^1]    |
| ServiceComponent          | Service      |

> Hilt doesn't generate a component for broadcast receivers because Hilt injects broadcast receivers directly from `SingletonComponent`.

[^1]: `View` annotated with `@WithFragmentBinding`.

## Component lifetimes

Hilt automatically creates and destroys instance following the lifecycle of the corresponding Android classes

| Generated component       | Created at             | Destroyed at          |
| ------------------------- | ---------------------- | --------------------- |
| SingletonComponent        | Application#onCreate() | Application destroyed |
| ActivityRetainedComponent | Activity#onCreate()    | Activity#onDestroy()  |
| ViewModelComponent        | ViewModel created      | ViewModel destroyed   |
| ActivityComponent         | Activity#onCreate()    | Activity#onDestroy()  |
| FragmentComponent         | Fragment#onAttach()    | Fragment#onDestroy()  |
| ViewComponent             | View#super()           | View destroyed        |
| ViewWithFragmentComponent | View#super()           | View destroyed        |
| ServiceComponent          | Service#onCreate()     | Service#onDestroy()   |

> ActivityRetainedComponent lives across configuration changes.

## Component Scoping

By default, all bindings in Hilt are unscoped. Each time your app requests an unscoped binding, Hilt creates a new instance of it.

However, Hilt can scope bindings to a particular component. Hilt creates a scoped component once per instance of the component that the binding is scoped to, and all request for that binding share the same instance.

> Scoping can be expensive because the provided object stays in memory until the container is destroyed.

The table below lists scoped annotations for each component:

| Android class                             | Generated component       | Scope                   |
| ----------------------------------------- | ------------------------- | ----------------------- |
| Application                               | SingletonComponent        | @Singleton              |
| Activity                                  | ActivityRetainedComponent | @ActivityRetainedScoped |
| ViewModel                                 | ViewModelComponent        | @ViewModelScoped        |
| Activity                                  | ActivityComponent         | @ActivityScoped         |
| Fragment                                  | FragmentComponent         | @FragmentScoped         |
| View                                      | ViewComponent             | @ViewScoped             |
| View annotated with @WithFragmentBindings | ViewWithFragmentComponent | @ViewScoped             |
| Service                                   | ServiceComponent          | @ServiceScoped          |

Scoping to the `ApplicationComponent` is equivalent to having an instance of that type in the application class.

```kotlin
@ActivityScoped
class AnalyticsAdapter @Inject constructor(
  private val service: AnalyticsService
) { ... }

// If AnalyticsService is an interface.
@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

  @Singleton
  @Binds
  abstract fun bindAnalyticsService(
    analyticsServiceImpl: AnalyticsServiceImpl
  ): AnalyticsService
}

// If you don't own AnalyticsService.
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

  @Singleton
  @Provides
  fun provideAnalyticsService(): AnalyticsService {
      return Retrofit.Builder()
               .baseUrl("https://example.com")
               .build()
               .create(AnalyticsService::class.java)
  }
}
```

In Android you can scope a type, without dependency injection, by using an instance variable of that type in a specific class.

```kotlin
// Without DI
class ExampleActivity : AppCompatActivity() {
  private val someClass = SomeClass()
  ...
}

// Hilt equivalent
@ActivityScoped
class SomeClass @Inject constructor() { ... }

@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() {

  @Inject
  lateinit var someClass: SomeClass
}
```

## Scoping with ViewModel

A ViewModel is an Architecture Component that survives configuration changes. In Android you can scope a dependency to
a ViewModel that will survive configuration changes like:

```kotlin
// Scoped a dependency to a ViewModel
class AnalyticsAdapter() { ... }
class ExampleViewModel() : ViewModel() {
  val analyticsAdapter = AnalyticsAdapter()
}
class ExampleActivity : AppCompatActivity() {
  private val viewModel: ExampleViewModel by viewModels()
  private val analyticsAdapter = viewModel.analyticsAdapter
}
```

In Hilt you can achieve the same scoping behavior which survives configuration changes like:

```kotlin
@ActivityRetainedScoped
class AnalyticsAdapter @Inject constructor() { ... }
@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() {
  @Inject lateinit var analyticsAdapter: AnalyticsAdapter
}
```

The `@ViewModelInject` annotation allows you to keep the ViewModel, while complying with good Dependency injection practices.

```kotlin
\\ Keeping the ViewModel
class AnalyticsAdapter @Inject constructor() { ... }

class ExampleViewModel @ViewModelInject constructor(
  val analyticsAdapter: AnalyticsAdapter
) : ViewModel() { ... }

@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() {
  private val viewModel: ExampleViewModel by viewModels()
  private val analyticsAdapter = viewModel.analyticsAdapter
}
```

## Component hierarchy

Installing a module into a component allows its bindings to be accessed as a dependency of other bindings in that component or in any child component below it in the component hierarchy.

## Multi-module apps

Hilt needs access to all the gradle modules that use Hilt. The Gradle module that compiles your `Application` class
needs to have all Hilt modules and constructor-injected classes in it's transitive dependencies.

Include the following in `app\build.gradle`:

```gradle
hilt {
  enableAggregatingTask = true
}
```

> Hilt requires special setup for feature modules because the dependencies are inverted.
