## Guide to app architecture

Common architectural principles

1. Separation of concerns

Ui-based classes, Activity and Fragment, should only contain logic to handle UI
and operating system interactions.

- improves testability
- avoids lifecycle issues
- minimizes dependency on Activity and Fragments which are not owned by developer
and controlled by OS.

Drive the UI from data models

- not tied to UI Elements and app components
- users don't lose data when OS frees up memory
- works when network connection is flaky or not available
- more testable
- more robust

Recommend app architecture

- Two Layers at least
1. UI layer: displays application data on the screen
2. Data layer: contains business logic of app and exposes the application data
3. Domain layer (optional): Use cases to simply and reuse interactions between UI and Data layers

UI Layer

- UI elements that render data on the screen. View or Compose
- State holders (ViewModels) that hold data, expose it to the UI, and handle logic.

Data layer

- contains the business logic
- rules for how the app creates, stores, and changes data
- made up of repositories that have zero to many data sources

Repositories:
- expose data
- centralize changes to data
- resolve conflicting data sources
- abstract sources of data
- contain business logic

Domain layer

optional layer that sits between UI and data layers

- encapsulates complex business logic
- simple reused logic by multiple ViewModels.
- not all apps have these requirements

classes in this layer are called `use cases` or `interactors`

- responsible for a single functionality

Managing dependencies between components

either:

- Dependency Inject (DI): allows classes to find dependencies at runtime. Another class is responsible for obtaining their dependencies.

- Service Locator: provides a registry where classes can obtain their dependencies instead of dependencies them.

- reduce duplication
- allow for test and production implementations of dependencies

General best practices

- Don't store data in components
- reduce dependencies on Android classes
- expose as little as possible in each module
- Focus on the unique core of your app so it stand out
- make each part of the app testable in isolation
- persist as much relevant and fresh data as possible


