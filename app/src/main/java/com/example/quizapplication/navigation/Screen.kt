package com.example.quizapplication.navigation

sealed class Screen(val route: String) {
    object Quiz : Screen("quiz")
    object Results : Screen("results")
}