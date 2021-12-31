# Hilt and ViewModels

## Injecting a ViewModel objects with Hilt

Provide a `ViewModel` by using the `@HiltViewModel` and the `@Inject` annotation in it's constructor.

```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val repository: Repository,
) : ViewModel() {
  ...
}
```

Then activities annotated with `@AndroidEntryPoint` can use the `ViewModel` instance using `ViewModelProvider` or the `by viewModels()` KTX extensions.

```kotlin
@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() {
  private val exampleViewModel: ExampleViewModel by viewModels()
  ...
}
```

If a single instance needs to be shared across multiple ViewModels, then it should be scoped to either `@ActivityRetainedScoped` or `@Singleton`.
