# Automate UI tests

UI Testings
: any test that verifies the UI behavior correctness. They are usually Instrumented test but can be local tests as well.

Testing user interacts ensures users have positive experiences with the app and do not encounter errors. Developers should get into the habit of writing UI tests.

Manual testing is time consuming and error-prone. Automated testing is fast and repeatable.

They are useful for:

- checking for regressions
- verifying compatibility with API levels and devices
- verifying the behavior of components or user work Flows

## Instrumented UI tests in Android Studio

The Android Plug-in for Gradle builds a test app, based on tests in the `src/androidTests/java`, folder and then runs the tests on the target device.

## Jetpack frameworks

Jetpack provides the following APIs for UI tests:

- _Espresso testing framework_: Simulating `View` interactions in a single app.
- _Jetpack Compose testing_: Testing APIs to launch and interact with Compose screens and components.
- _UI Automator_: APIs for testing cross-app and system UI functionality. Allows you to perform actions such as opening the Settings menu or the app launcher on the test device.
- _Robolectric_: Runs you run local tests on a JVM instead of an emulator or device. It can use Espresso or Compose testing APIs with UI components.
