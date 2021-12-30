# Test Basics

Local Tests (test source set):
unit tests do not require an emulator or physical device. Run fast.

Instrumented Tests (androidTest source set):
slower real world tests

## Unit Test

```gradle
// Dependencies for local unit tests
testImplementation "junit:junit:$junitVersion"

// Other dependencies
testImplementation "org.hamcrest:hamcrest-all:$hamcrestVersion" or Truth


```

```kotlin
// a test class is just a normal class
class ExampleTest {
    // each test is annotated with @Test (this is a Junit annotation)
    @Test
    fun addition_isCorrect() {
        // plain old Junit
        assertEquals(4, 2 + 2)

        // with Hamcrest
        // http://hamcrest.org/JavaHamcrest/tutorial
        assertThat(2 + 2, `is`(4))
    }
}
```

Tests:
- are a class in test source sets
- contain functions annotated with @Test
- usually contain assertion statements

Android uses JUnit for testing, provides @Test and assertions

assertion: checks code behaves as expected

## Instrumented Test

androidTest source set

needs or benefits from being run on an emulated or real device

```kotlin
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.android.architecture.blueprints.reactive",
            appContext.packageName)
    }
}
```

Generate > Test

Given, When, Then

One way to think about the structure of a test is to follow the Given, When, Then testing mnemonic. This divides your test into three parts:

Given: Setup the objects and the state of the app that you need for your test. For this test, what is "given" is that you have a list of tasks where the task is active.

When: Do the actual action on the object you're testing.

Then: This is where you actually check what happens when you do the action where you check if the test passed or failed. This is usually a number of assert function calls. 

Note that the" Arrange, Act, Assert" (AAA) testing mnemonic is a similar concept.

Test Names

Test names are meant to be descriptive. 

## Test Driven Development

write test first before writing the feature

Red, Green, Refactor

- confirm test fails

## ViewModels

should not rely on Android

- deps 

```gradle
// AndroidX Test - JVM testing
testImplementation "androidx.test.ext:junit-ktx:$androidXTestExtKotlinRunnerVersion"

testImplementation "androidx.test:core-ktx:$androidXTestCoreVersion"

testImplementation "org.robolectric:robolectric:$robolectricVersion"
```

Note: If your view model does not need an Application context, you can construct the view model without needing any additional libraries.

add Test Runner

```kotlin
@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {
    // Test code
    @Test
    fun addNewTask_setsNewTaskEvent() {

        // Given a fresh ViewModel
        val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event is triggered
        // TODO test LiveData
    }
}
```

AndroidX Test APIs

work for both Unit Tests and Instrumented tests

ApplicationProvider.getApplicationContext()

Robolectric: simulated Android environment for local tests

required for 
```gradle
  testOptions.unitTests {
        includeAndroidResources = true

        // ... 
    }
```

## LiveData

use InstantTaskExecutorRule
use observeForever

```gradle
testImplementation "androidx.arch.core:core-testing:$archTestingVersion"
```

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

util class to remove above boilerplate

LiveDataTestUtil.kt
```koltin
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


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

example

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

@Before use fore setup

```koltin
// Subject under test
private lateinit var tasksViewModel: TasksViewModel

@Before
fun setupViewModel() {
    tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
}
```

