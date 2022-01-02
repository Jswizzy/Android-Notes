# Test Basics

## Definitions

Local Tests (test source set)
: unit tests do not require an emulator or physical device. Run fast.

Instrumented Tests (androidTest source set)
: slower real world tests

Assertions
: checks code behaves as expected

## Benefits of Testing

Testing helps to verify the correctness of your app, functional behavior, and usability prior to release to the public.

Testing also provides the following advantages:

- Rapid feedback on failures.
- Early failure detection in the development cycle.
- Safer code refactoring, allowing for optimizations without regressions.
- Stable development velocity, minimizing technical debt.

You can **manually** test your app.However, manual testing does not scale well and can overlook regressions. **Automated testing** provides feedback faster and earlier than manual testing.

## Types of tests in Android

Mobile applications are complex and are required to work in many different environments.

### Subject

Test types for a particular subject:

- _Functional tests_: Tests the Behavior of the app.
- _Performance tests_: Tests the performance and efficiency of the app.
- _Accessibility tests_: Tests how well the app works for the handicapped and disabled users.
- _Compatibility tests_: Tests how well the app works on different devices and at different API levels.

## Scope

Tests depending on size and degree of isolation:

- _Unit tests_: verify behavior of a class, method, or function.
- _End-to-End tests_: Tests a screen or user flow.
- _Integration tests_: Tests interaction between two or more units.

## Instrumented vs local tests

The most important test distinction for a developer is where the test is run.

- _Instrumented tests_
  : run on an Android device, emulated and or physical. Usually tests UI and user interactions.

- _Local tests_
  : executed on a development machine or server. Called Host-side tests. Usually they are small and fast tests and isolated from the rest of the app.

## Testable architecture

Code is structured so that its parts can be tested easily in isolation.App Architecture is focused on testability.

### Decoupling

The most important concept in testable architecture is decoupling.

Decoupling techniques:

- Use layers and modules such as Presentation, Domain, Data.
- Avoid putting large dependencies in activities and fragments.
  Move **business logic** into Composables, ViewModels, or the domain layer.
- Avoid direct framework dependencies in classes containing business logic such as using a Context in a ViewModel.
- Make dependencies interchangeable by using interfaces and Dependency Injection. Use **Hilt** or Manual Dependency Injection.
- Do not hard-code dependencies, instead use Dependency Injection.

## Test Isolation

Unit tests should only test the method, function, or class subject.

**Isolation**
: isolating the 'unit' under test and only testing that part.

**Dependency Injection**
: Providing dependencies, two forms Constructor and Parameter Injection.

**Constructor Injection**
: passing dependencies in the constructor, allows for injecting test doubles.

_DefaultTasksRepository.kt_

```kotlin
// Wihtout Dependency Injection
class DefaultTasksRepository private constructor(application: Application) {

    private val tasksRemoteDataSource: TasksDataSource
    private val tasksLocalDataSource: TasksDataSource

   // Some other code

    init {
        val database = Room.databaseBuilder(application.applicationContext,
            ToDoDatabase::class.java, "Tasks.db")
            .build()

        tasksRemoteDataSource = TasksRemoteDataSource
        tasksLocalDataSource = TasksLocalDataSource(database.taskDao())
    }
    // Rest of class
}
```

refactoring to use Dependency Injection

```kotlin
// REPLACE
class DefaultTasksRepository private constructor(application: Application) { // Rest of class }

// WITH

class DefaultTasksRepository(
    private val tasksRemoteDataSource: TasksDataSource,
    private val tasksLocalDataSource: TasksDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
      // Rest of class
    }
```
