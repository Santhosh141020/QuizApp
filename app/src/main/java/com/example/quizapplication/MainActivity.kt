package com.example.quizapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.quizapplication.data.QuizViewModel
import com.example.quizapplication.ui.theme.QuizApplicationTheme

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // Observe questions
        viewModel.questions.observe(this) { questions ->
            questions?.forEach { question ->
                Log.i(TAG, "Question ${question.id}: ${question.question}")
                Log.i(TAG, "Options: ${question.options}")
                Log.i(TAG, "Correct Answer Index: ${question.correctOptionIndex}")
                Log.i(TAG, "")

            }
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            Log.d(TAG, "Loading state: $isLoading")
        }

        // Observe errors
        viewModel.error.observe(this) { error ->
            error?.let {
                Log.e(TAG, "Error: $it")
            }
        }

        Log.d(TAG, "Starting to fetch questions...")
        viewModel.fetchQuestions()

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuizApplicationTheme {
        Greeting("Android")
    }
}