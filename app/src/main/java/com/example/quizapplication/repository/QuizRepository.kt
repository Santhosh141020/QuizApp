package com.example.quizapplication.repository

import com.example.quizapplication.data.Question
import com.example.quizapplication.network.RetrofitInstance

class QuizRepository {
    suspend fun getQuestions(): List<Question> {
        return RetrofitInstance.api.getQuestions()
    }
}