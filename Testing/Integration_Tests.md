# Integration Tests

Integration Tests
: checks how parts of the app work together.

## When to write integration tests

prefer unit tests, write integration tests only when you need to interact with another part of the app or an external element.

## When tests require the Android Framework

There are two options:

- run them on a device or emulation
- use `Robolectric`
