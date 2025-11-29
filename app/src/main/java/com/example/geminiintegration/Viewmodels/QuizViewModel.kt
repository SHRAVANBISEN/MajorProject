package com.example.studentlearning.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiintegration.DataModels.Quiz
import com.example.geminiintegration.DataModels.QuizQuestion
import com.example.geminiintegration.DataModels.QuizResult
import com.example.geminiintegration.DataModels.StudyContent
import com.example.geminiintegration.DataModels.UserProfile
import com.example.geminiintegration.Repositories.LearningRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class QuizUiState {
    object Initial : QuizUiState()
    object Loading : QuizUiState()
    data class QuizReady(val quiz: Quiz) : QuizUiState()
    data class QuizCompleted(val result: QuizResult) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}

class QuizViewModel : ViewModel() {

    private val repository = LearningRepository()

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Initial)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var currentQuiz: Quiz? = null
    private val passingPercentage = 80

    fun generateQuiz(userProfile: UserProfile, studyContent: StudyContent) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading

            val result = repository.generateQuiz(userProfile, studyContent)

            _uiState.value = result.fold(
                onSuccess = { quiz ->
                    currentQuiz = quiz
                    QuizUiState.QuizReady(quiz)
                },
                onFailure = {
                    QuizUiState.Error(it.message ?: "Failed to generate quiz")
                }
            )
        }
    }

    fun selectAnswer(questionIndex: Int, answerIndex: Int) {
        currentQuiz?.let { quiz ->
            if (questionIndex in quiz.questions.indices) {
                // Create a new list of questions with the updated selection
                val updatedQuestions = quiz.questions.mapIndexed { index, question ->
                    if (index == questionIndex) {
                        // Create a new question object with updated selectedAnswerIndex
                        question.copy(selectedAnswerIndex = answerIndex)
                    } else {
                        question
                    }
                }

                // Create a new Quiz object
                val updatedQuiz = quiz.copy(questions = updatedQuestions)
                currentQuiz = updatedQuiz

                // Emit the new state to trigger recomposition
                _uiState.value = QuizUiState.QuizReady(updatedQuiz)
            }
        }
    }

    fun submitQuiz() {
        currentQuiz?.let { quiz ->
            val allAnswered = quiz.questions.all { it.selectedAnswerIndex != null }

            if (!allAnswered) {
                _uiState.value = QuizUiState.Error("Please answer all questions before submitting")
                return
            }

            val result = repository.evaluateQuiz(quiz, passingPercentage)
            _uiState.value = QuizUiState.QuizCompleted(result)
        }
    }

    fun getPassingPercentage(): Int = passingPercentage
}