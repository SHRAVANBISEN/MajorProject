package com.example.geminiintegration.DataModels


data class UserProfile(
    val standard: String,          // e.g., "10th Grade"
    val subject: String,            // e.g., "Mathematics"
    val topic: String,              // e.g., "Quadratic Equations"
    val learningLevel: String,      // "Beginner", "Intermediate", "Advanced"
    val additionalPrompt: String = "" // Any extra instructions
)