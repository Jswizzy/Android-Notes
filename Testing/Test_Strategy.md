# Test Plans

When designing a test strategy there are three aspects to consider:

- _Scope_: How much code is tested.
- _Speed_: How fast the test runs.
- _Fidelity_: How "real-world" is the test.

## Organization

Android apps contain two directories for testing:

- `androidTest` directory for tests that require an Android device emulated or real to run. These include integration and end-to-end test.

- `test` directory for tests that can be run locally such as unit tests.

## Essential Unit Tests

Unit tests should be written for the following:

- _ViewModels_ and Presenters.
- _data layer_ such as repositories. Most of the Data Layer should be platform independent and interchangeable with test doubles and fakes.
- _Domain layer_, such as use cases and interactions.
- _Utility classes_

## Unit testing Edge Cases

Edge cases are unlikely to be caught by a human tester. Such as: `Android

- Network errors
- math operations
- corrupt data, such as malformed Json
- full storage when saving a file
- Objection created in the middle of a process, such as a device rotation.

## Unit Tests to Avoid

Low value tests:

- Testing someone elses code, such as libraries and frameworks.
- Framework entry points such as activities, fragments, or services because they shouldn't have business logic. They should be covered by Instrumented tests such as UI tests can cover these classes instead.

## UI Test

You should Test:

- _Screen UI tests_ check user interactions in a screen.

- _User flow tests_ tests, covering common paths. These tests simulate a user using the app. They should be simple tests, useful for checking for run-time crashes in initialization.
