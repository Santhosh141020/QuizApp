package com.example.quizapplication.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapplication.repository.QuizRepository
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {
    private val TAG  = "QuizViewModel"

    private val repository = QuizRepository() // In a real app, you'd inject this

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Quiz state management
    private val _currentQuestionIndex = MutableLiveData<Int>()
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _selectedAnswer = MutableLiveData<Int?>()
    val selectedAnswer: LiveData<Int?> = _selectedAnswer

    private val _showAnswer = MutableLiveData<Boolean>()
    val showAnswer: LiveData<Boolean> = _showAnswer

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    private val _streak = MutableLiveData<Int>()
    val streak: LiveData<Int> = _streak

    private val _streakMessage = MutableLiveData<String?>()
    val streakMessage: LiveData<String?> = _streakMessage

    private val _highestStreak = MutableLiveData<Int>()
    val highestStreak: LiveData<Int> = _highestStreak

    init {
        _currentQuestionIndex.value = 0
        _selectedAnswer.value = null
        _showAnswer.value = false
        _score.value = 0
        _streak.value = 0
        _streakMessage.value = null
        _highestStreak.value = 0
    }

    fun fetchQuestions() {
        viewModelScope.launch {
            try {
                _isLoading.postValue(true)
                _error.postValue(null)
                
                val questionList = repository.getQuestions()
                _questions.postValue(questionList)
                Log.d(TAG, "Success! Fetched ${questionList.size} questions from Repository.")
            } catch (e: Exception) {
                Log.e(TAG, "API call failed: ${e.message}")
                _error.postValue("Failed to load questions: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun selectAnswer(answerIndex: Int) {
        if (_showAnswer.value == true) return // Don't allow selection after answer is shown
        
        _selectedAnswer.value = answerIndex
        _showAnswer.value = true
        
        // Check if answer is correct
        val currentQuestion = _questions.value?.get(_currentQuestionIndex.value ?: 0)
        val isCorrect = answerIndex == currentQuestion?.correctOptionIndex
        
        if (isCorrect) {
            _score.value = (_score.value ?: 0) + 1
            val newStreak = (_streak.value ?: 0) + 1
            _streak.value = newStreak
            
            // Update highest streak
            val currentHighest = _highestStreak.value ?: 0
            if (newStreak > currentHighest) {
                _highestStreak.value = newStreak
            }
            
            // Check for streak achievements
            checkStreakAchievement(newStreak)
        } else {
            _streak.value = 0
            _streakMessage.value = null
        }
        
        Log.d(TAG, "Answer selected: $answerIndex, Correct: $isCorrect, Score: ${_score.value}, Streak: ${_streak.value}")
    }

    private fun checkStreakAchievement(streak: Int) {
        when (streak) {
            3 -> _streakMessage.value = "3 questions streak achieved !!"
            5 -> _streakMessage.value = "5 questions streak achieved !!"
            7 -> _streakMessage.value = "7 questions streak achieved !!"
            10 -> _streakMessage.value = "Perfect! 10 questions streak achieved !!"
            else -> _streakMessage.value = null
        }
        Log.d(TAG, "Streak achievement check: streak=$streak, message=${_streakMessage.value}")
    }

    fun nextQuestion() {
        val currentIndex = _currentQuestionIndex.value ?: 0
        val totalQuestions = _questions.value?.size ?: 0
        
        if (currentIndex < totalQuestions - 1) {
            _currentQuestionIndex.value = currentIndex + 1
            _selectedAnswer.value = null
            _showAnswer.value = false
            _streakMessage.value = null // Clear streak message when moving to next question
            Log.d(TAG, "Moving to next question, clearing streak message")
        }
    }

    fun skipQuestion() {
        nextQuestion()
    }

    fun getCurrentQuestion(): Question? {
        val index = _currentQuestionIndex.value ?: 0
        return _questions.value?.getOrNull(index)
    }

    fun isLastQuestion(): Boolean {
        val currentIndex = _currentQuestionIndex.value ?: 0
        val totalQuestions = _questions.value?.size ?: 0
        return currentIndex >= totalQuestions - 1
    }
}