# UI Events

## Definitions

UI:
View-based or Compose code that handles user interface

UI Events:
user interactions

User Events:
Events that the user produces when interacting with the app.

## Handling User Events

The ViewModel is responsible for handling business logic for user events. If an event updates data in the screen it should be handled by the ViewModel.
User events can also trigger Ui Behavior such as navigation that is handled by the UI directly.
Business logic is usually the same on different platforms

![UI event decision tree](https://developer.android.com/topic/libraries/architecture/images/mad-arch-uievents-tree.png)

If an event updates data in the screen it should be handled by the ViewModel.

```kotlin
@Composable
fun NewsApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "latestNews",
    ) {
        composable("latestNews") {
            MyScreen(
                // The navigation event is processed by calling the NavController
                // navigate function that mutates its internal state.
                onProfileClick = { navController.navigate("profile") }
            )
        }
        /* ... */
    }
}

@Composable
fun LatestNewsScreen(
    viewModel: LatestNewsViewModel = viewModel(),
    onProfileClick: () -> Unit
) {
    Column {
        // The refresh event is processed by the ViewModel that is in charge
        // of the UI's business logic.
        Button(onClick = { viewModel.refreshNews() }) {
            Text("Refresh data")
        }
        Button(onClick = onProfileClick) {
            Text("Profile")
        }
    }
}
```

## Unidirectional Data Flow

UI actions that originate from the ViewModel—ViewModel events—should always result in a UI state update.

ViewModel events should always result in UI state updates.

> You can recreate state after process death with Saved State Module

```kotlin
data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUserLoggedIn: Boolean = false
)

class LoginViewModel : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set
    /* ... */
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onUserLogIn: () -> Unit
) {
    val currentOnUserLogIn by rememberUpdatedState(onUserLogIn)

    // Whenever the uiState changes, check if the user is logged in.
    LaunchedEffect(viewModel.uiState)  {
        if (viewModel.uiState.isUserLoggedIn) {
            currentOnUserLogIn()
        }
    }

    // Rest of the UI for the login screen.
}
```

## Consuming events

Consuming events in the `ViewModel` can result in additional UI state changes.

For example displaying a message to the user:

```kotlin
// Models the message to show on the screen.
data class UserMessage(val id: Long, val message: String)

// Models the UI state for the Latest news screen.
data class LatestNewsUiState(
    val news: List<News> = emptyList(),
    val isLoading: Boolean = false,
    val userMessages: List<UserMessage> = emptyList()
)

class LatestNewsViewModel(/* ... */) : ViewModel() {

    var uiState by mutableStateOf(LatestNewsUiState())
        private set

    fun refreshNews() {
        viewModelScope.launch {
            // If there isn't internet connection, show a new message on the screen.
            if (!internetConnection()) {
                val messages = uiState.userMessages + UserMessage(
                    id = UUID.randomUUID().mostSignificantBits,
                    message = "No Internet connection"
                )
                uiState = uiState.copy(userMessages = messages)
                return@launch
            }

            // Do something else.
        }
    }

    fun userMessageShown(messageId: Long) {
        val messages = uiState.userMessages.filterNot { it.id == messageId }
        uiState = uiState.copy(userMessages = messages)
    }
}
```

The `ViewModel` only needs to know there is a message but doesn't need to know anything about rendering it.

```
@Composable
fun LatestNewsScreen(
    snackbarHostState: SnackbarHostState,
    viewModel: LatestNewsViewModel = viewModel(),
) {
    // Rest of the UI content.

    // If there are user messages to show on the screen,
    // show the first one and notify the ViewModel.
    viewModel.uiState.userMessages.firstOrNull()?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.message)
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.userMessageShown(userMessage.id)
        }
    }
}
```

## Principles of UI Layer

1. Each class should do what they're responsible for, not more. The UI is in charge of screen-specific behavior logic such as navigation calls, click events, and obtaining permission requests. The ViewModel contains business logic and converts the results from lower layers of the hierarchy into UI state.

2. Think about where the event originates. Follow the decision tree presented at the beginning of this guide, and make each class handle what they're responsible for. For example, if the event originates from the UI and it results in a navigation event, then that event has to be handled in the UI. Some logic might be delegated to the ViewModel, but handling the event can’t be entirely delegated to the ViewModel.

3. If you have multiple consumers and you're worried about the event being consumed multiple times, you might need to reconsider your app architecture. Having multiple concurrent consumers results in the delivered exactly once contract becoming extremely difficult to guarantee, so the amount of complexity and subtle behavior explodes. If you're having this problem, consider pushing those concerns upwards in your UI tree; you might need a different entity scoped higher up in the hierarchy.

4. Think about when the state needs to be consumed. In certain situations, you might not want to keep consuming state when the app is in the background—for example, showing a Toast. In those cases, consider consuming the state when the UI is in the foreground.
