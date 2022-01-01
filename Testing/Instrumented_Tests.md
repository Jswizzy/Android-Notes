## Instrumented Tests

---

Instrumented Tests are tests that need or benefits from being run on an emulated or real device. They are more accurate than Units but take longer to run.

Instrumented tests are stored in the `androidTest source set`

```kotlin
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.android.architecture.blueprints.reactive",
            appContext.packageName)
    }
}
```
