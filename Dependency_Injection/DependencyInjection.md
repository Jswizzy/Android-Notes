# Dependency Injection

Dependency Injection
: a technique used in programming to provide dependencies to a library without coupling dependencies.

Dependency Injection is an essential part for testable architecture.

There are two types of dependency injection, **constructor injection** and **property injection**.

A container is the entity that assembles the dependencies.

```kotlin
// Without Dependency Injection
class Vehicle() {
  private val engine = CombustionEngine()

  fun start(): Boolean {
    ...
    return engine.start()
  }
  ...
}

// Constructor injection
class Vehicle(private val engine: Engine) {
  fun start(): Boolean {
    ...
    return engine.start()
  }
  ...
}

// Property injection
class Vehicle {
  var engine: Engine? = null
  ...
  fun start(): Boolean {
    engine?.let {
      return engine.start()
    }
    return false
  }
  ...
}
```
