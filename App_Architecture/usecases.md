# Use cases

show intent of software. Describe behavior of a system and its interactions with
users.

clean architecture

a use case is a description of a software's usage.

Use Case Diagram

also know as interactor

application-specific

input output contract

does not know who called it

describes rules

use case does not describe the UI

should be called by a component handling inputs to the app, e.g., UI or Controller,
not Repository

exposed as a single flow

```kotlin
class SignInUseCase {
  suspend fun signInWithEmail(email: String, password: String): User {
    ...
  }
  suspend fun signInWithGoogle(serverAuthCode: String?): User {
    ...
  }
}

```

one business rule interaction only, don't mix

```kotlin
class ViewProfileUseCase(
  private val dataSource: DataSource
) {
  suspend fun getUserProfile(): User {
    return dataSource.getCurrentUser()
  }
}

class ChangePasswordUseCase {
  suspend fun changePassword(current: String, new: String): User {
    ...
  }
}
```


