# Service Locator

Service Locator is an alternative pattern to Dependency Injection. It uses a singleton to supply dependencies to regular code and for testing purposes. Useful when Dependency Injection through a constructor is not possible.

## Creating a Service Locator

```kotlin
object ServiceLocator {

    private var database: ToDoDatabase? = null
    @Volatile
    var tasksRepository: TasksRepository? = null

    fun provideTasksRepository(context: Context): TasksRepository {
        // Needs to be thread safe
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        val newRepo = DefaultTasksRepository(TasksRemoteDataSource, createTaskLocalDataSource(context))
        tasksRepository = newRepo
        return newRepo
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = database ?: createDataBase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    private fun createDataBase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java, "Tasks.db"
        ).build()
        database = result
        return result
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
