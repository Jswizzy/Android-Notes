## Domain Layer

optional layer that sits between the UI and Data layer

encapsulates complex business logic, used by multiple ViewModels 

only use when needed

benefits:

- avoids code duplication
- improves readability
- improves testability
- avoids large classes by splitting responsibilities

Use Cases:

- immutable
- responsible for a single functionality

Naming conventions

verb in present tense + noun/what (optional) + UseCase

ex: FormatDataUseCase, LogOutUserUseCase, GetLatestNewsWithAuthorUseCase

## Dependencies

usually depend on repositories may also depend on other use cases

api exposed via coroutines (kotlin) or callbacks (java)

```kotlin
class GetLatestNewsWithAuthorsUseCase(
  private val newsRepository: NewsRepository,
  private val authorsRepository: AuthorsRepository,
  private val formatDateUseCase: FormatDateUseCase
) { /* ... */ }
```

Use cases in Kotlin

can take advantage of `invoke()` with the `operator` modifier.

```kotlin
class FormatDateUseCase(userRepository: UserRepository) {

    private val formatter = SimpleDateFormat(
        userRepository.getPreferredDateFormat(),
        userRepository.getPreferredLocale()
    )

    operator fun invoke(date: Date): String {
        return formatter.format(date)
    }
}
```

allows for calling Use Case as if it was a function

```koltin
class MyViewModel(formatDateUseCase: FormatDateUseCase) : ViewModel() {
    init {
        val today = Calendar.getInstance()
        val todaysDate = formatDateUseCase(today)
        /* ... */
    }
}
```

## Lifecycles

Use cases are scoped to the classes that use them.

- data should be immutable
- create a new instance of a use case class every time it is passed as a dependency

## Threading

- must be main-safe
- complex computations placed in data layer to encourage reusability and caching

```kotlin
class MyUseCase(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(...) = withContext(defaultDispatcher) {
        // Long-running blocking operations happen on a background thread.
    }
}
```

## Common tasks

reusable simple business logic

- encapsulate repeatable business logic in a use case class
- formatting is a good example
- could be a util class but util classes are hard to find and discover
- can share threading and error handling
- can be used by services and domain layer

combine repositories 

- combine repositories into a use case to increase readability
- easier to test in isolation and reuse
- Room relationships might be a better option

```kotlin
/**
 * This use case fetches the latest news and the associated author.
 */
class GetLatestNewsWithAuthorsUseCase(
  private val newsRepository: NewsRepository,
  private val authorsRepository: AuthorsRepository,
  private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(): List<ArticleWithAuthor> =
        withContext(defaultDispatcher) {
            val news = newsRepository.fetchLatestNews()
            val result: MutableList<ArticleWithAuthor> = mutableListOf()
            // This is not parallelized, the use case is linearly slow.
            for (article in news) {
                // The repository exposes suspend functions
                val author = authorsRepository.getAuthor(article.authorId)
                result.add(ArticleWithAuthor(article, author))
            }
            result
        }
}
```

## Testing

use fake repositories
