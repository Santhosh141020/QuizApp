package com.example.quizapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quizapplication.QuizScreen
import com.example.quizapplication.ResultsScreen
import com.example.quizapplication.data.QuizViewModel

@Composable
fun QuizNavigation(
    navController: NavHostController,
    viewModel: QuizViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Quiz.route
    ) {
        composable(Screen.Quiz.route) {
            QuizScreen(
                viewModel = viewModel,
                onNavigateToResults = {
                    navController.navigate(Screen.Results.route)
                }
            )
        }
        
        composable(Screen.Results.route) {
            ResultsScreen(
                viewModel = viewModel,
                onNavigateToQuiz = {
                    navController.popBackStack()
                }
            )
        }
    }
}
