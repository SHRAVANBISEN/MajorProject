package com.example.geminiintegration.Repositories

import com.example.geminiintegration.DataModels.Quiz
import com.example.geminiintegration.DataModels.QuizResult
import com.example.geminiintegration.DataModels.StudyContent
import com.example.geminiintegration.DataModels.UserProfile
import com.example.geminiintegration.Utils.GeminiHelper
import com.example.geminiintegration.Utils.QuizParser


class LearningRepository {

    suspend fun generateContent(
        userProfile: UserProfile,
        attemptNumber: Int = 1
    ): Result<StudyContent> {
        return try {
            val difficulty = when {
                attemptNumber > 2 -> "easy"
                userProfile.learningLevel.lowercase() == "beginner" -> "easy"
                userProfile.learningLevel.lowercase() == "advanced" -> "hard"
                else -> "medium"
            }

            val content = GeminiHelper.generateStudyContent(
                standard = userProfile.standard,
                subject = userProfile.subject,
                topic = userProfile.topic,
                difficulty = difficulty,
                attemptNumber = attemptNumber,
                additionalPrompt = userProfile.additionalPrompt
            )

            Result.success(
                StudyContent(
                    topic = userProfile.topic,
                    content = content,
                    difficulty = difficulty,
                    attemptNumber = attemptNumber
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateQuiz(
        userProfile: UserProfile,
        studyContent: StudyContent
    ): Result<Quiz> {
        return try {
            val quizJson = GeminiHelper.generateQuiz(
                standard = userProfile.standard,
                subject = userProfile.subject,
                topic = userProfile.topic,
                studyContent = studyContent.content
            )

            val quiz = QuizParser.parseQuizJson(quizJson)
            if (quiz != null && quiz.questions.isNotEmpty()) {
                Result.success(quiz)
            } else {
                Result.failure(Exception("Failed to parse quiz"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun evaluateQuiz(quiz: Quiz, passingPercentage: Int = 80): QuizResult {
        val totalQuestions = quiz.questions.size
        val correctAnswers = quiz.questions.count {
            it.selectedAnswerIndex == it.correctAnswerIndex
        }
        val percentage = (correctAnswers.toDouble() / totalQuestions) * 100

        return QuizResult(
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            percentage = percentage,
            passed = percentage >= passingPercentage
        )
    }
}