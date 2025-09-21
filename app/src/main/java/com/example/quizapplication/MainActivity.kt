package com.example.quizapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import com.example.quizapplication.data.QuizViewModel
import com.example.quizapplication.navigation.QuizNavigation
import com.example.quizapplication.ui.theme.QuizApplicationTheme
import com.example.quizapplication.ui.theme.CorrectGreen
import com.example.quizapplication.ui.theme.IncorrectRed
import com.example.quizapplication.ui.theme.FeedbackGreenLight
import com.example.quizapplication.ui.theme.FeedbackRedLight
import com.example.quizapplication.ui.theme.FeedbackGreenDark
import com.example.quizapplication.ui.theme.FeedbackRedDark
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.unit.sp
import com.example.quizapplication.data.Question

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizApplicationTheme {
                val navController = rememberNavController()
                QuizNavigation(
                    navController = navController,
                    viewModel = viewModel
                )
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
fun QuizScreen(
    viewModel: QuizViewModel,
    onNavigateToResults: () -> Unit
) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    val questions by viewModel.questions.observeAsState()
    val error by viewModel.error.observeAsState()
    val isQuizCompleted by viewModel.isQuizCompleted.observeAsState(false)

    // Navigate to results when quiz is completed
    LaunchedEffect(isQuizCompleted) {
        if (isQuizCompleted) {
            onNavigateToResults()
        }
    }

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
    val answerFeedback by viewModel.answerFeedback.observeAsState()
    
    val currentQuestion = questions?.getOrNull(currentQuestionIndex)
    val totalQuestions = questions?.size ?: 0
    
    // Auto-advance after showing answer
    LaunchedEffect(showAnswer) {
        if (showAnswer) {
            delay(2000) // Wait 2 seconds before auto-advancing
            viewModel.nextQuestion()
        }
    }
    
    if (currentQuestion != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with progress
            QuizHeader(
                currentQuestion = currentQuestionIndex + 1,
                totalQuestions = totalQuestions,
                streak = streak,
                streakMessage = streakMessage
            )

            Spacer(modifier = Modifier.height(8.dp))


            // Question Card
            QuestionCard(
                question = currentQuestion,
                selectedAnswer = selectedAnswer,
                showAnswer = showAnswer,
                onAnswerSelected = { answerIndex ->
                    viewModel.selectAnswer(answerIndex)
                }
            )

            if (showAnswer && answerFeedback != null) {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    AnswerFeedbackCard(
                        feedback = answerFeedback ?: "",
                        isCorrect = selectedAnswer == currentQuestion.correctOptionIndex
                    )
                }
            }
            
            // Skip Button
            if (!showAnswer) {
                TextButton(
                    onClick = { viewModel.skipQuestion() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Skip", fontSize = 14.sp)
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
            progress = { currentQuestion/ totalQuestions.toFloat() },
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
    question: Question,
    selectedAnswer: Int?,
    showAnswer: Boolean,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "cardScale"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .scale(scale),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Question Text with animation
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Answer Options with staggered animation
            question.options.forEachIndexed { index, option ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(400, delayMillis = index * 100)
                    ) + fadeIn(animationSpec = tween(400, delayMillis = index * 100))
                ) {
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
    var isPressed by remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "buttonScale"
    )
    
    val buttonColors by animateFloatAsState(
        targetValue = when {
            showAnswer && isCorrect -> 1f // Green
            showAnswer && isSelected && !isCorrect -> 2f // Red
            isSelected -> 3f // Secondary
            else -> 0f // Default
        },
        animationSpec = tween(300),
        label = "buttonColor"
    )
    
    val animatedColors = when (buttonColors) {
        1f -> CorrectGreen to Color.White
        2f -> IncorrectRed to Color.White
        3f -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            containerColor = animatedColors.first,
            contentColor = animatedColors.second
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
fun AnswerFeedbackCard(
    feedback: String,
    isCorrect: Boolean,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) {
                if (isDarkTheme) FeedbackGreenDark else FeedbackGreenLight
            } else {
                if (isDarkTheme) FeedbackRedDark else FeedbackRedLight
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = feedback,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isCorrect) {
                if (isDarkTheme) Color(0xFF81C784) else Color(0xFF2E7D32)
            } else {
                if (isDarkTheme) Color(0xFFEF5350) else Color(0xFFC62828)
            },
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ResultsScreen(
    viewModel: QuizViewModel,
    onNavigateToQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val score by viewModel.score.observeAsState(0)
    val questions by viewModel.questions.observeAsState()
    val highestStreak by viewModel.highestStreak.observeAsState(0)
    
    val totalQuestions = questions?.size ?: 0
    val percentage = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
    
    // Animated score counter
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(1000),
        label = "scoreAnimation"
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Results Header with animation
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(800)
            ) + fadeIn(animationSpec = tween(800))
        ) {
            Text(
                text = "Quiz Complete! 🎉",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Score Card with animation
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(
                animationSpec = spring(dampingRatio = 0.6f)
            ) + fadeIn(animationSpec = tween(600))
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your Score",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "${animatedScore.toInt()} / $totalQuestions",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Performance Stats with staggered animation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(600, delayMillis = 200))
            ) {
                StatCard(
                    title = "Highest Streak",
                    value = highestStreak.toString(),
                    icon = "🔥"
                )
            }
            
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(600, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(600, delayMillis = 400))
            ) {
                StatCard(
                    title = "Accuracy",
                    value = "$percentage%",
                    icon = "🎯"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Action Buttons with animation
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(600, delayMillis = 600)
            ) + fadeIn(animationSpec = tween(600, delayMillis = 600))
        ) {
            Button(
                onClick = { 
                    viewModel.restartQuiz()
                    onNavigateToQuiz()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Play Again")
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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