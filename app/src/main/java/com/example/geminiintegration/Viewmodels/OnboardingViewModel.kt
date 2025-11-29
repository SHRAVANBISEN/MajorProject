package com.example.studentlearning.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.geminiintegration.DataModels.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel : ViewModel() {

    private val _standard = MutableStateFlow("")
    val standard: StateFlow<String> = _standard.asStateFlow()

    private val _subject = MutableStateFlow("")
    val subject: StateFlow<String> = _subject.asStateFlow()

    private val _topic = MutableStateFlow("")
    val topic: StateFlow<String> = _topic.asStateFlow()

    private val _learningLevel = MutableStateFlow("Intermediate")
    val learningLevel: StateFlow<String> = _learningLevel.asStateFlow()

    private val _additionalPrompt = MutableStateFlow("")
    val additionalPrompt: StateFlow<String> = _additionalPrompt.asStateFlow()

    // Predefined options
    val standardOptions = listOf(
        "6th Grade", "7th Grade", "8th Grade", "9th Grade",
        "10th Grade", "11th Grade", "12th Grade"
    )

    val subjectOptions = listOf(
        "Mathematics", "Physics", "Chemistry", "Biology",
        "English", "History", "Geography", "Computer Science"
    )

    val learningLevelOptions = listOf(
        "Beginner", "Intermediate", "Advanced"
    )

    fun updateStandard(value: String) {
        _standard.value = value
    }

    fun updateSubject(value: String) {
        _subject.value = value
    }

    fun updateTopic(value: String) {
        _topic.value = value
    }

    fun updateLearningLevel(value: String) {
        _learningLevel.value = value
    }

    fun updateAdditionalPrompt(value: String) {
        _additionalPrompt.value = value
    }

    fun createUserProfile(): UserProfile {
        return UserProfile(
            standard = _standard.value,
            subject = _subject.value,
            topic = _topic.value,
            learningLevel = _learningLevel.value,
            additionalPrompt = _additionalPrompt.value
        )
    }

    fun isFormValid(): Boolean {
        return _standard.value.isNotBlank() &&
                _subject.value.isNotBlank() &&
                _topic.value.isNotBlank()
    }
    fun resetForm() {
        _standard.value = ""
        _subject.value = ""
        _topic.value = ""
        _learningLevel.value = "Intermediate"
        _additionalPrompt.value = ""
    }
}