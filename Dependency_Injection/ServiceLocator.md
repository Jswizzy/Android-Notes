# Service Locator

Service Locator is an alternative pattern to Dependency Injection. It uses a singleton to supply dependencies to regular code and for testing purposes. Useful when Dependency Injection through a constructor is not possible.

## Creating a Service Locator

```kotlin
class ServiceLocator(applicationContext: Context) {

    private val logsDatabase = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "logging.db"
    ).build()

    val loggerLocalDataSource = LoggerLocalDataSource(logsDatabase.logDao())

    fun provideDateFormatter() = DateFormatter()

    fun provideNavigator(activity: FragmentActivity): AppNavigator {
        return AppNavigatorImpl(activity)
    }
}
```

## Using a Service Locator

```kotlin
class TodoApplication : Application() {

    val taskRepository: TasksRepository
        get() = ServiceLocator.provideTasksRepository(this)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
    }
}
```

## Replacing Manual Dependency Injection

```kotlin
// DELETE THIS COMPANION OBJECT
companion object {
    @Volatile
    private var INSTANCE: DefaultTasksRepository? = null

    fun getRepository(app: Application): DefaultTasksRepository {
        return INSTANCE ?: synchronized(this) {
            val database = Room.databaseBuilder(app,
                ToDoDatabase::class.java, "Tasks.db")
                .build()
            DefaultTasksRepository(TasksRemoteDataSource, TasksLocalDataSource(database.taskDao())).also {
                INSTANCE = it
            }
        }
    }
}

// REPLACE this code
private val viewModel by viewModels<TaskDetailViewModel> {
    TaskDetailViewModelFactory(DefaultTasksRepository.getRepository(requireActivity().application))
}

// WITH this code

private val viewModel by viewModels<TaskDetailViewModel> {
    TaskDetailViewModelFactory((requireContext().applicationContext as TodoApplication).taskRepository)
}
```
