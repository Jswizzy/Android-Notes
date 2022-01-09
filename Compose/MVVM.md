# Model-View-ViewModel (MVVM)

An event based approach to a graphic application. ViewModel provides observable data and the view consumes it. The ViewModel knows nothing about the view. The ViewModel shouldn't have any dependencies on the Android UI framework.

Consists of:

- Model
  : Business logic and data layer. (same as MVP/MVC)
- View
  : Notifies ViewModel of user interactions and subscribes to and displays data exposed by the ViewModel.
- ViewModel
  : Retrieves and updates the data from the model and exposes it as observable streams.
