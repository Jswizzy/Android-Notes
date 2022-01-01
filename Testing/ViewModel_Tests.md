# Testing ViewModels

ViewModel tests should not rely on Android

## Dependencies

```gradle
// AndroidX Test - JVM testing
testImplementation "androidx.test.ext:junit-ktx:$androidXTestExtKotlinRunnerVersion"

testImplementation "androidx.test:core-ktx:$androidXTestCoreVersion"

testImplementation "org.robolectric:robolectric:$robolectricVersion"
```

## Writing Tests

Add the Test Runner to test:

> Note: If your view model does not need an Application context, you can construct the view model without needing any additional libraries.

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
