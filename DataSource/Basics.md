# DataStore

Jetpack library that provides a safe and consistent API for asynchronously storing small amounts of data, such as preferences and application state, using Kotlin coroutines and Flow.

Replaces SharedPreferences.

Advantages over SharedPreferences:

- Thread safe
- Non-blocking
- Proto DataStore, version is Type Safe
- migration support
- data consistency
- error handling

There are two different implementations of:

Proto DataStore
: stores typed objects backed by protocol buffers.

Preferences DataStore
: stores key-value pairs.

## Async API

SharedPreferences offers:

`OnSharedPreferencesChangeListener` for asynchronous notification of updates but the callback is invoked on the main thread.

`apply` offloads saving to background.

DataStore is a _fully asynchronous API_ using Kotlin coroutines and Kotlin Flows.

## Error Handling

`SharedPreferences` can throw parsing errors as runtime exceptions.

Most common crash is `ClassCastException`.

DataStore relies on Flow's error signaling mechanism.

## Type safety

Proto DataStore gives you _full type safety_ via a predefined schema for your data model.

## Data consistency

DataStore's API is _fully transactional_ providing ACID guarantees. Data is updated in an _atomic read-modify-write_ operation.

## Migration support

DataStore provides migration support and implementations `SharedPreferences`-to-DataStore migration unlike `SharedPreferences`.
