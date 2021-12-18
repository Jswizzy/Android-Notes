# Data Layer

- contains application data and business logic
- separation of concerns
- made up of repositories that contain zero to many data sources

## Repositories

- create a repository for each data type
- immutable
- use dependency injection to inject datasources

responsible for:

- exposing data to the rest of the app
- centralizing changes to the data
- resolving conflicts between multiple data sources
- abstracting sources of data from the rest of the app
- containing business logic

## Data Source

- should handle only one source of data
- can be a file. network source, or a local database.
- bridge app and system for data ops
- entry point is the repository
- never accessed by state holders or use cases

```kotlin
class ExampleRepository(
    private val exampleRemoteDataSource: ExampleRemoteDataSource, // network
    private val exampleLocalDataSource: ExampleLocalDataSource // database
) { /* ... */ }
```

exposing APIs

- One-shot operations: The data layer should expose suspend functions in Kotlin; and for the Java programming language, the data layer should expose functions that provide a callback to notify the result of the operation, or RxJava Single, Maybe, or Completable types.
- To be notified of data changes over time: The data layer should expose flows in Kotlin; and for the Java programming language, the data layer should expose a callback that emits the new data, or the RxJava Observable or Flowable type.

```kotlin
class ExampleRepository(
    private val exampleRemoteDataSource: ExampleRemoteDataSource, // network
    private val exampleLocalDataSource: ExampleLocalDataSource // database
) {

    val data: Flow<Example> = ...

    suspend fun modifyData(example: Example) { ... }
}
```

Naming conventions:

data + Repository

ex: NewsRepository

data + type of source + DataSource

ex: NewsRemoteDataSource, NewsLocalDataSource

- don't name dataSources based on implementation detail, harder to change implementation

Multiple levels of repositories

- repositories can depend on other repositories
- some developers call repositories that depend on multiple repositories managers. ex: UserManager

Source of truth

Threading

- main safe
- room
- coroutines
- retrofit

Lifecycle

- scope
- use di

## caching data

caches: meant to save some information in memory for a specific amount of time.

can be implemented in the repository or data source classes.

```kotlin
class NewsRepository(
  private val newsRemoteDataSource: NewsRemoteDataSource
) {
    // Mutex to make writes to cached values thread-safe.
    private val latestNewsMutex = Mutex()

    // Cache of the latest news got from the network.
    private var latestNews: List<ArticleHeadline> = emptyList()

    suspend fun getLatestNews(refresh: Boolean = false): List<ArticleHeadline> {
        if (refresh || latestNews.isEmpty()) {
            val networkResult = newsRemoteDataSource.fetchLatestNews()
            // Thread-safe write to latestNews
            latestNewsMutex.withLock {
                this.latestNews = networkResult
            }
        }

        return latestNewsMutex.withLock { this.latestNews }
    }
}
```

## Lifecycle

navigating away from the screen shouldn't cancel the network request.

- Fetching data should be an app-orientated operation.
- attach use Coroutine attached to repository
- use DI for coroutine
- use Dispatchers.Default

```kotlin
class NewsRepository(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    // this could be CoroutineScope(SupervisorJob() + Dispatchers.Default).
    private val externalScope: CoroutineScope
) {
    /* ... */

    suspend fun getLatestNews(refresh: Boolean = false): List<ArticleHeadline> {
        return if (refresh) {
            externalScope.async {
                newsRemoteDataSource.fetchLatestNews().also { networkResult ->
                    // Thread-safe write to latestNews.
                    latestNewsMutex.withLock {
                        latestNews = networkResult
                    }
                }
            }.await()
        } else {
            return latestNewsMutex.withLock { this.latestNews }
        } 
    }
}
```

async is used to start the coroutine in the external scope. await is called on the new coroutine to suspend until the network request comes back and the result is saved to the cache. If by that time the user is still on the screen, then they will see the latest news; if the user moves away from the screen, await is canceled but the logic inside async continues to execute.

## Save and retrieve data

for data that needs to survive process death and to be accessible when not connected to the network

survive process death:
- Large datasets: store in Room database
- small datasets: use DataStore
- chunks of data like a json object: use a file

classes that use data shouldn't know how to save it

DataStore: used to store key-value pairs like settings

ex: time format, notification preferences, show ui items

- can use protocol buffers
- exposes a flow

Scheduling tasks with WorkManager

WorkManager: schedule asynchronous and reliable work and can take care of constraint management.

- recommended for persistent work 
tasks are performed by workers. ex: Fetch + Period + Data + Worker

```koltin
class RefreshLatestNewsWorker(
    private val newsRepository: NewsRepository,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = try {
        newsRepository.refreshLatestNews()
        Result.success()
    } catch (error: Throwable) {
        Result.failure()
    }
}
```

- The business logic for this type of task should be encapsulated in its own class and treated as a separate data source.

```koltin
private const val REFRESH_RATE_HOURS = 4L
private const val FETCH_LATEST_NEWS_TASK = "FetchLatestNewsTask"
private const val TAG_FETCH_LATEST_NEWS = "FetchLatestNewsTaskTag"

class NewsTasksDataSource(
    private val workManager: WorkManager
) {
    fun fetchNewsPeriodically() {
        val fetchNewsRequest = PeriodicWorkRequestBuilder<RefreshLatestNewsWorker>(
            REFRESH_RATE_HOURS, TimeUnit.HOURS
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.TEMPORARILY_UNMETERED)
                .setRequiresCharging(true)
                .build()
        )
            .addTag(TAG_FETCH_LATEST_NEWS)

        workManager.enqueueUniquePeriodicWork(
            FETCH_LATEST_NEWS_TASK,
            ExistingPeriodicWorkPolicy.KEEP,
            fetchNewsRequest.build()
        )
    }

    fun cancelFetchingNewsPeriodically() {
        workManager.cancelAllWorkByTag(TAG_FETCH_LATEST_NEWS)
    }
}
```

These types of classes are named after the data they're responsible for—for example, NewsTasksDataSource or PaymentsTasksDataSource. All tasks related to a particular type of data should be encapsulated in the same class.

If the task needs to be triggered at app startup, it's recommended to trigger the WorkManager request using the App Startup library that calls the repository from an Initializer.

## Testing

- use dependency injection
- rely on interfaces for classes that communicate with external resources

Unit Tests

- can use fakes for external sources

Integration test

- less reliable if using external sources
- Room allows for creating an in-memory database
