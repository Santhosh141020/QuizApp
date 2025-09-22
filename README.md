# 🧠 Quiz Application

### Implementation Overview

This Quiz Application is built using modern Android development practices with **Jetpack Compose** as the primary UI framework. The app follows the **MVVM (Model-View-ViewModel)** architecture pattern, ensuring clean separation of concerns and maintainable code structure.

The user interface is entirely constructed using **Jetpack Compose**, which provides a declarative approach to building UIs. All screens, including the quiz interface, results screen, and loading states, are implemented as Composable functions. This allows for reactive UI updates and seamless state management throughout the application.

**Navigation** is handled through **Navigation Compose** using a `NavHost` component that manages the flow between different screens. The navigation system supports type-safe navigation with sealed classes defining the available routes, ensuring compile-time safety and preventing navigation errors.

The **ViewModel** layer (`QuizViewModel`) manages all the business logic and application state using **LiveData** for reactive programming. The ViewModel handles quiz progression, scoring, streak tracking, and answer validation. It communicates with the UI through observable LiveData properties, ensuring that the Compose UI automatically recomposes when the underlying data changes.

**State management** is implemented using a combination of LiveData and Compose's state management capabilities. The ViewModel exposes LiveData properties that are observed in the Compose UI using `observeAsState()`, creating a reactive data flow from the data layer to the presentation layer.

The **data layer** follows the Repository pattern, with `QuizRepository` acting as a single source of truth for data operations. The repository communicates with the remote API using **Retrofit** and **Gson** for JSON serialization, fetching quiz questions from a remote endpoint and providing them to the ViewModel.

### Project Structure

```
quizapplication/
├── data/
│   ├── Question.kt  
│   └── QuizViewModel.kt  
├── network/
│   ├── ApiService.kt   
│   └── RetrofitInstance.kt  
├── navigation/
│   ├── QuizNavigation.kt 
│   └── Screen.kt  
├── repository/
│   └── QuizRepository.kt 
├── ui/theme/
│   ├── Color.kt   
│   ├── Theme.kt  
│   └── Type.kt  
├── Constants.kt  
└── MainActivity.kt  
```
