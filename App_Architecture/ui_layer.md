# UI Layer

1. consume app data and transform it into data the ui can render
2. consume Ui data and transform it into UI elements for presentation to enduser
3. consume user input events and reflect their effects in the UI data as needed
4. repeat

## UI STATE 

- what the app says the user should see. reflect in the UI
- Immutability
- prevent multiple sources of truth

*naming conventions*

functionality + UiState

ex: `NewsUiState`

****Ui* what the users sees
*UiState* immutable snapshot of the detials needed to render the Ui

- transformations 

*unidirectional data flow (UDF)*
- helps enforce seperation of responsibility
- separates the place where state changes originate, the place where they are transformed, and the place where they are finally consumed

benifits of UDF:
1. Data consistency: There is a single source of truth for the Ui
2. Testability: The source of state is isolated and therefor testable independent of the Ui
3. Maintainability: Mutation of state follows a well-defined pattern where mutations are a result of
both user events adn the sources of data they pull from.

*StateHolder* viewModel or classes responsible for production of the UiState

- ViewModel is the recommend implementation for the management of screen-level
with access to the data layer

pattern state flows down, events, events flow up

Flow
- viewModel holds and exposes UiState
- ui notifies the viewModel of user events
- viewModel handles user actions and updates UiState
- state feed back to UI to render
- repeat

Treats the state as a stream

- observable use LiveData or StateFlow

*Business logic* what to do with state changes
- domain or data layer

*Ui behavior logic*
- how to display state changes on the screen
- should live in the UI not the ViewModel, esp if it contains a context
- can be a simple class functioning as a state holder

Simple classes created in the UI can take Android SDK dependencies because they
follow the lifecycle of the Ui; ViewModels have longer lifespans.

- viewModelScope: used for launching coroutines to perform async work

```kotlin
class NewsViewModel(
    private val repository: NewsRepository,
    ...
) : ViewModel() {

   var uiState by mutableStateOf(NewsUiState())
        private set

    private var fetchJob: Job? = null

    fun fetchArticles(category: String) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val newsItems = repository.newsItemsForCategory(category)
                uiState = uiState.copy(newsItems = newsItems)
            } catch (ioe: IOException) {
                // Handle the error and notify the notify the UI when appropriate.
                val messages = getMessagesFromThrowable(ioe)
                uiState = uiState.copy(userMessages = messages)
            }
        }
    }
}
```

```kotlin
data class NewsUiState(
    val isSignedIn: Boolean = false,
    val isPremium: Boolean = false,
    val newsItems: List<NewsItemUiState> = listOf()
)

val NewsUiState.canBookmarkNews: Boolean get() = isSignedIn && isPremium
```

- single or multiple steams? Group like states. Diffing, unrelated data types

## consuming the Ui state

- observe or collect
- LiveData and LifecycleOwner take care of lifecycle concerns
- Flows require appropriate coroutine adn repeatOnLifecycle Api

```kotlin
@Composable
fun LatestNewsScreen(
    viewModel: NewsViewModel = viewModel()
) {
    // Show UI elements based on the viewModel.uiState
}
```

# showing in-progress

- UiState with a boolean field

```kotlin
data class NewsUiState(
    val isFetchingArticles: Boolean = false,
    ...
)
```

```kotlin
@Composable
fun LatestNewsScreen(
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = viewModel()
) {
    Box(modifier.fillMaxSize()) {

        if (viewModel.uiState.isFetchingArticles) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        // Add other UI elements. For example, the list.
    }
}
```

## showing errors

- might want to model with a class

```kotlin
data class Message(val id: Long, val message: String)

data class NewsUiState(
    val userMessages: List<Message> = listOf(),
    ...
)
```

## threading and concurrency

- main safe or moved to background via coroutines

## navigation

- event driven
- state might have a field that triggers navigation
- use Navigation component

## Paging

- paging library
- expose in it's own stream
- not immutable


