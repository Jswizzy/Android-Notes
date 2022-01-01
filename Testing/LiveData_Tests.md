# Testing LiveData

## LiveData Dependencies

To test LiveData Android provides `InstantTaskExecutorRule` and `observeForever`.

```gradle
testImplementation "androidx.arch.core:core-testing:$archTestingVersion"
```

## LiveData Test Example

```kotlin
class TasksViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Other code...
    @Test
    fun addNewTask_setsNewTaskEvent() {

    // Given a fresh ViewModel
    val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())


    // Create observer - no need for it to do anything!
    val observer = Observer<Event<Unit>> {}
    try {

        // Observe the LiveData forever
        tasksViewModel.newTaskEvent.observeForever(observer)

        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event is triggered
        val value = tasksViewModel.newTaskEvent.value
        assertThat(value?.getContentIfNotHandled(), (not(nullValue())))

    } finally {
        // Whatever happens, don't forget to remove the observer!
        tasksViewModel.newTaskEvent.removeObserver(observer)
    }
}
```

## LiveData Testing Utility Class

A Utility class to remove above boilerplate

LiveDataTestUtil.kt

```kotlin
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}
```

An example of its usage:

```kotlin
@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Test
    fun addNewTask_setsNewTaskEvent() {
        // Given a fresh ViewModel
        val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event is triggered
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()

        assertThat(value.getContentIfNotHandled(), not(nullValue()))


    }

}
```
