package com.example.quizapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                QuizApp(viewModel = viewModel)
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
fun QuizApp(viewModel: QuizViewModel) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when {
            isLoading -> {
                LoadingScreen(modifier = Modifier.padding(innerPadding))
            }
            error != null -> {
                ErrorScreen(
                    error = error!!,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}



@Composable
fun ErrorScreen(error: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error Loading Questions",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Loading Quiz Questions...",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please wait while we fetch the questions",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}