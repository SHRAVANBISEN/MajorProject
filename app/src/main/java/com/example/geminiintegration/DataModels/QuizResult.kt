package com.example.geminiintegration.DataModels

data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val percentage: Double,
    val passed: Boolean
) {
    val score: String
        get() = "$correctAnswers/$totalQuestions"
}