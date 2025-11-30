package com.example.studentlearning.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiintegration.DataModels.StudyContent
import com.example.geminiintegration.DataModels.UserProfile
import com.example.geminiintegration.Repositories.LearningRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class StudyUiState {
    object Initial : StudyUiState()
    object Loading : StudyUiState()
    data class Success(val content: StudyContent) : StudyUiState()
    data class Error(val message: String) : StudyUiState()
}

class StudyViewModel : ViewModel() {

    private val repository = LearningRepository()

    private val _uiState = MutableStateFlow<StudyUiState>(StudyUiState.Initial)
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    private var currentUserProfile: UserProfile? = null
    private var attemptNumber = 1

    fun generateContent(userProfile: UserProfile, retry: Boolean = false) {
        currentUserProfile = userProfile

        if (retry) {
            attemptNumber++
        } else {
            attemptNumber = 1
        }

        viewModelScope.launch {
            _uiState.value = StudyUiState.Loading

            val result = repository.generateContent(userProfile, attemptNumber)

            _uiState.value = result.fold(
                onSuccess = { StudyUiState.Success(it) },
                onFailure = { StudyUiState.Error(it.message ?: "Failed to generate content") }
            )
        }
    }

    fun regenerateContent() {
        currentUserProfile?.let { profile ->
            generateContent(profile, retry = true)
        }
    }

    fun getAttemptNumber(): Int = attemptNumber

    // ADD THIS FUNCTION
    fun resetViewModel() {
        _uiState.value = StudyUiState.Initial
        currentUserProfile = null
        attemptNumber = 1
    }
}