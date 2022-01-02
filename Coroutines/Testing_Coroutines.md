# Testing Coroutines

## Dependencies

```gradle
testImplementation "org.jetbrains.kotlin:kotlinx-coroutines-test:$version"
```

## runTest

The runTest() method allows for testing coroutines.
It will automatically skip calls to delay() and handle uncaught exceptions. Call runTest() only once per test.

```kotlin
@Test
fun testF() = runTest { // Run a coroutine with virtual time
   val actual = f()
   val expected = 42
   assertEquals(actual, expected)
}

suspend fun f(): Int {
   delay(1_000) // Will finish immediately instead of delaying
   return 42
}
```
