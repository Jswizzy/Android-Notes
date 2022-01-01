# Test doubles

Tests are conducted in isolation. However, the subject under test might depend on external dependencies to be tested. Rather than providing a real dependency it is common to provide a test double instead. Test doubles have the advantage of making tests faster and simpler to execute.

Test Double
: An object that looks and acts as a component of an app but is used for testing to provide a specific behavior or data.

## Types of test doubles

| Type   | Description                                                                                                                                                                  |
| ------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Fake   | "Working" implementation that is good for testing but unsuitable for production. Example: an in-memory database. Fakes don't require mocking and are lightweight. Preferred. |
| Mock   | A mock is a type of test double that has expectations about its interactions, and whose behavior you can define. Example: verify that a database was called exactly once.    |
| Stub   | A test double that behaves as you program but doesn't have expectations interactions. Fakes are simpler adn preferred'                                                       |
| Dummy  | A test double that is passed around but not used. For example, passing an empty function to a callback.                                                                      |
| Spy    | A wrapper around a real object that tracks additional information, similar to a mock. Adds complexity. Fakes and Mocks preferred.                                            |
| Shadow | Robolectric Fake                                                                                                                                                             |

## Example Fake

Implementing an repository using an interface to test a ViewModel

```kotlin
// A Fake implementation of an Interface
object FakeRepository : UserRepository {
  fun getUsers() = listOf(UserAlice, UserBob)
}

val const UserAlice = User("Alice")
val const UserBob = User("Bob")
```

This fake doesn't depend on a local or remote data source. File lives in the `test sources set` which is not included in the production app.

```kotlin
// Using the Fake to test a ViewModel
@Test
fun `viewModel loads showFirstUser`() {
  // Given a ViewModel using a fake
  val viewModel = AViewModel(FakeRepository)

  // Verify
  assertEquals(viewModel.firstUserName, UserAlice.name)
}
```

## Using Dependency Injection.

Helps manage replacing components with test doubles. Follows testable design principles.

Big end-to-end tests benefit from the use of test doubles, such as instrumented UI tests that verify user flows. The type of tests can benefit from being hermetic.

Hermetic test
: A test that does not fetch data from the internet, improving reliability and performance.

See Hilt testing for more information.

## Robolectric

A framework that simulates an Android device providing fakes know as shadows. Allows test to be run locally.

Pros:

- Run tests locally
- Can test UI
- Fast and reduced costs
- Compatible with Compose and Espresso tests
- Good for testing functionality

Cons:

- Not suitable for simple unit tests
- Not suitable for compatibility tests
- Lower Fidelity than testing on a device
