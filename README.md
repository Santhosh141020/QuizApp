# 🧠 Quiz Application

A modern, interactive Android quiz application built with Jetpack Compose, featuring smooth animations, dark theme support, and comprehensive user feedback.

## ✨ Features

### 🎯 Core Functionality

- **Interactive Quiz Interface**: Answer multiple-choice questions with immediate feedback
- **Real-time Scoring**: Track your score and performance throughout the quiz
- **Streak Tracking**: Monitor consecutive correct answers with achievement messages
- **Auto-advance**: Questions automatically advance after showing feedback
- **Skip Questions**: Option to skip questions you're unsure about

### 🎨 User Experience

- **Color-coded Feedback**:
  - ✅ Green for correct answers
  - ❌ Red for incorrect answers
  - Immediate visual feedback with smooth animations
- **Smooth Animations**:
  - Staggered entrance animations for answer options
  - Press feedback with scale animations
  - Slide and fade transitions between screens
- **Gesture Support**: Interactive tap gestures for enhanced user experience

### 🌙 Theme Support

- **Dark Theme**: Complete dark mode support with system theme detection
- **Light Theme**: Clean, modern light theme
- **Consistent Colors**: Theme-aware color system throughout the app
- **Material 3 Design**: Following latest Material Design guidelines

### 📊 Results & Analytics

- **Performance Summary**: Detailed results screen with score and statistics
- **Animated Score Counter**: Score animates from 0 to final score
- **Performance Metrics**:
  - Total score and percentage
  - Highest streak achieved
  - Accuracy percentage
- **Play Again**: Easy restart functionality

### 🧭 Navigation

- **Navigation Compose**: Modern navigation system
- **Screen Management**: Separate screens for quiz and results
- **Smooth Transitions**: Automatic navigation when quiz completes

## 🏗️ Architecture

### Implementation Overview

This Quiz Application is built using modern Android development practices with **Jetpack Compose** as the primary UI framework. The app follows the **MVVM (Model-View-ViewModel)** architecture pattern, ensuring clean separation of concerns and maintainable code structure.

The user interface is entirely constructed using **Jetpack Compose**, which provides a declarative approach to building UIs. All screens, including the quiz interface, results screen, and loading states, are implemented as Composable functions. This allows for reactive UI updates and seamless state management throughout the application.

**Navigation** is handled through **Navigation Compose** using a `NavHost` component that manages the flow between different screens. The navigation system supports type-safe navigation with sealed classes defining the available routes, ensuring compile-time safety and preventing navigation errors.

The **ViewModel** layer (`QuizViewModel`) manages all the business logic and application state using **LiveData** for reactive programming. The ViewModel handles quiz progression, scoring, streak tracking, and answer validation. It communicates with the UI through observable LiveData properties, ensuring that the Compose UI automatically recomposes when the underlying data changes.

**State management** is implemented using a combination of LiveData and Compose's state management capabilities. The ViewModel exposes LiveData properties that are observed in the Compose UI using `observeAsState()`, creating a reactive data flow from the data layer to the presentation layer.

The **data layer** follows the Repository pattern, with `QuizRepository` acting as a single source of truth for data operations. The repository communicates with the remote API using **Retrofit** and **Gson** for JSON serialization, fetching quiz questions from a remote endpoint and providing them to the ViewModel.

**Theming and styling** are implemented using Material 3 design system with custom color schemes and typography. The app supports both light and dark themes with automatic system theme detection, providing a consistent and modern user experience across different device configurations.

### Project Structure

```
app/src/main/java/com/example/quizapplication/
├── data/
│   ├── Question.kt              # Data model for quiz questions
│   └── QuizViewModel.kt         # ViewModel for quiz logic and state
├── network/
│   ├── ApiService.kt            # Retrofit API interface
│   └── RetrofitInstance.kt      # Retrofit configuration
├── navigation/
│   ├── QuizNavigation.kt        # Navigation setup
│   └── Screen.kt                # Screen definitions
├── repository/
│   └── QuizRepository.kt        # Data repository
├── ui/theme/
│   ├── Color.kt                 # Color definitions
│   ├── Theme.kt                 # Theme configuration
│   └── Type.kt                  # Typography definitions
├── Constants.kt                 # App constants
└── MainActivity.kt              # Main activity and UI composables
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 24 or higher
- Kotlin 2.0.21 or later
- Gradle 8.12.3 or later

### Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd QuizApplication
   ```
2. **Open in Android Studio**

   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the QuizApplication folder and select it
3. **Sync the project**

   - Android Studio will automatically sync the project
   - Wait for the sync to complete
4. **Run the app**

   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift + F10`

### Build Configuration

The app is configured with:

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 14)
- **Compile SDK**: 36
- **Java Version**: 11

## 🔧 Configuration

### API Configuration

The app fetches questions from a remote API. To modify the API endpoint:

1. Open `Constants.kt`
2. Update the `subUrl` constant with your API endpoint
3. Ensure the API returns data in the expected format:

```json
[
  {
    "id": 1,
    "question": "What is the capital of France?",
    "options": ["London", "Paris", "Berlin", "Madrid"],
    "correctOptionIndex": 1
  }
]
```
