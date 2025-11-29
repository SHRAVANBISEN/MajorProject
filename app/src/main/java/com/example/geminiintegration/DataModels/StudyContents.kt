package com.example.geminiintegration.DataModels


data class StudyContent(
    val topic: String,
    val content: String,
    val difficulty: String,        // "easy", "medium", "hard"
    val attemptNumber: Int = 1     // How many times content was regenerated
)