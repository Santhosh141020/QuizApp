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
}