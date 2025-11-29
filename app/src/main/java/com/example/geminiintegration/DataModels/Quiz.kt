package com.example.geminiintegration.DataModels

data class Quiz(
    val questions: List<QuizQuestion>
)

data class QuizQuestion(
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    var selectedAnswerIndex: Int? = null
)