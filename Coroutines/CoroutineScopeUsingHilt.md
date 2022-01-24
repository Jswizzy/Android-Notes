# Creating an application CoroutineScope using Hilt

## Manual Dependency Injection

manual scope injection:

```kotlin
class MyRepository(private val externalScope: CoroutineScope) { ... }

class MyApp : Application() {

  val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
  val myRepository = MyRepository(applicationScope)
}
```

using a container to separate concerns:

```kotlin
class ApplicationDiContainer {
  val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
  val myRepository = MyRepository(applicationScope)
}

class MyApp : Application() {
  val applicationDiContainer = ApplicationDiContainer()
}
```

## Hilt Dependency Injection

```kotlin
@HiltAndroidApp
class App : Application()

@Singleton
class MyRepository @Inject constructor(
  private val externalScope: CoroutineScope
) {
  /* ... */
}
```

providing types with a module:

```kotlin
@InstallIn(SingletonComponent::class)
@Module
object CoroutineScopeModule {

  @Singleton
  @Provides
  fun providesCoroutineScope(): CoroutineScope {
    return CoroutineScope(SupervisorJob() + Dispatchers.Default)
  }
}
```

Not hardcoded:

```kotlin
// CoroutinesQualifiers.kt file

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class DefaultDispatcher

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class MainDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainImmediateDispatcher
```

```kotlin
@InstallIn(SingletonComponent::class)
@Module
object CoroutinesDispatchersModule {

    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @MainImmediateDispatcher
    @Provides
    fun providesMainImmediateDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate
}
```

```kotlin
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@InstallIn(SingletonComponent::class)
@Module
object CoroutinesScopeModule {

  @Singleton
  @ApplicationScope
  @Provides
  fun providesCoroutineScope(
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
  ): CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)
}
```

using:

```kotlin
@Singleton
class MyRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope
) { /* ... */ }
```
