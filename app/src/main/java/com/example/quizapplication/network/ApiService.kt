package com.example.quizapplication.network

import com.example.quizapplication.Constants
import com.example.quizapplication.data.Question
import retrofit2.http.GET

interface ApiService {
    @GET(Constants.subUrl)
    suspend fun getQuestions(): List<Question>
}