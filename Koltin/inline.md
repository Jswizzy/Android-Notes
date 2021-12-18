## Inline

`inline` keyword

tells Kotlin compliler to copy bytecode for a function directly into the binary whenever the method is called, instead of generating a single compiled version.

```kotlin
inline fun<T, R> Array<out T>.map(transfrom: (T) -> R): List<R>
```

- reduces the overhead of extra object allocation that some lambda expressions require
