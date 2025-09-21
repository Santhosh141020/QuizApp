package com.example.quizapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val questions by viewModel.questions.observeAsState()
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
            !questions.isNullOrEmpty() -> {
                QuestionScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                LoadingScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}



@Composable
fun QuestionScreen(viewModel: QuizViewModel, modifier: Modifier = Modifier) {
    val currentQuestionIndex by viewModel.currentQuestionIndex.observeAsState(0)
    val questions by viewModel.questions.observeAsState()
    val selectedAnswer by viewModel.selectedAnswer.observeAsState()
    val showAnswer by viewModel.showAnswer.observeAsState(false)
    val streak by viewModel.streak.observeAsState(0)
    val streakMessage by viewModel.streakMessage.observeAsState()
    
    val currentQuestion = questions?.getOrNull(currentQuestionIndex)
    val totalQuestions = questions?.size ?: 0
    
    if (currentQuestion != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with progress
            QuizHeader(
                currentQuestion = currentQuestionIndex + 1,
                totalQuestions = totalQuestions,
                streak = streak,
                streakMessage = streakMessage
            )
            
            // Question Card
            QuestionCard(
                question = currentQuestion,
                selectedAnswer = selectedAnswer,
                showAnswer = showAnswer,
                onAnswerSelected = { answerIndex ->
                    viewModel.selectAnswer(answerIndex)
                }
            )
            
            // Skip Button
            if (!showAnswer) {
                TextButton(
                    onClick = { viewModel.skipQuestion() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Skip")
                }
            }
            
            // Next Question Button (shown after answer is selected)
            if (showAnswer && !viewModel.isLastQuestion()) {
                Button(
                    onClick = { viewModel.nextQuestion() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next Question")
                }
            }
        }
    }
}

@Composable
fun QuizHeader(
    currentQuestion: Int,
    totalQuestions: Int,
    streak: Int,
    streakMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Top row with Quiz title and Streak indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quiz",
                style = MaterialTheme.typography.headlineMedium
            )
            
            // Streak flame indicators (4 flames max)
            StreakFlames(streak = streak)
        }
        
        // Streak achievement message
        if (streakMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = streakMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Progress section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question $currentQuestion of $totalQuestions",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Linear Progress
        LinearProgressIndicator(
            progress = { currentQuestion.toFloat() / totalQuestions.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun StreakFlames(streak: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Only show flames when there's an active streak
        if (streak > 0) {
            repeat(minOf(streak, 4)) { index ->
                Text(
                    text = "🔥",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun QuestionCard(
    question: com.example.quizapplication.data.Question,
    selectedAnswer: Int?,
    showAnswer: Boolean,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Question Text
            Text(
                text = question.question,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Answer Options
            question.options.forEachIndexed { index, option ->
                AnswerButton(
                    text = option,
                    index = index,
                    isSelected = selectedAnswer == index,
                    isCorrect = index == question.correctOptionIndex,
                    showAnswer = showAnswer,
                    onClick = { onAnswerSelected(index) }
                )
            }
        }
    }
}

@Composable
fun AnswerButton(
    text: String,
    index: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    showAnswer: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColors = when {
        showAnswer && isCorrect -> MaterialTheme.colorScheme.primary
        showAnswer && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = when {
        showAnswer && isCorrect -> MaterialTheme.colorScheme.onPrimary
        showAnswer && isSelected && !isCorrect -> MaterialTheme.colorScheme.onError
        isSelected -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            containerColor = buttonColors,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
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