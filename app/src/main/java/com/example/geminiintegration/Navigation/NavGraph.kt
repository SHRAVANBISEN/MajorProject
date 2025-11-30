package com.example.geminiintegration.Navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.geminiintegration.DataModels.QuizResult
import com.example.geminiintegration.DataModels.StudyContent
import com.example.geminiintegration.DataModels.UserProfile

import com.example.studentlearning.ui.screens.*
import com.example.studentlearning.ui.viewmodels.*

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object StudyContent : Screen("study_content")
    object Quiz : Screen("quiz")
    object Result : Screen("result")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Shared state across screens
    var currentUserProfile by remember { mutableStateOf<UserProfile?>(null) }
    var currentStudyContent by remember { mutableStateOf<StudyContent?>(null) }
    var currentQuizResult by remember { mutableStateOf<QuizResult?>(null) }

    // ViewModels that persist across navigation
    val onboardingViewModel: OnboardingViewModel = viewModel()
    val studyViewModel: StudyViewModel = viewModel()
    val quizViewModel: QuizViewModel = viewModel()

    // Function to reset everything
    fun resetAllState() {
        currentUserProfile = null
        currentStudyContent = null
        currentQuizResult = null
        onboardingViewModel.resetForm()
        studyViewModel.resetViewModel()
        quizViewModel.resetViewModel()
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route
    ) {
        // Onboarding Screen
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onStartLearning = { userProfile ->
                    currentUserProfile = userProfile
                    navController.navigate(Screen.StudyContent.route)
                },
                viewModel = onboardingViewModel
            )
        }

        // Study Content Screen
        composable(Screen.StudyContent.route) {
            currentUserProfile?.let { profile ->
                // Observe study state
                val studyState by studyViewModel.uiState.collectAsState()

                // Store current study content when successful
                LaunchedEffect(studyState) {
                    if (studyState is StudyUiState.Success) {
                        currentStudyContent = (studyState as StudyUiState.Success).content
                    }
                }

                StudyContentScreen(
                    userProfile = profile,
                    onTakeQuiz = {
                        navController.navigate(Screen.Quiz.route)
                    },
                    onBackToHome = {
                        resetAllState()
                        navController.popBackStack(Screen.Onboarding.route, inclusive = false)
                    },
                    viewModel = studyViewModel
                )
            }
        }

        // Quiz Screen
        composable(Screen.Quiz.route) {
            currentUserProfile?.let { profile ->
                currentStudyContent?.let { content ->
                    // Observe quiz state
                    val quizState by quizViewModel.uiState.collectAsState()

                    // Store quiz result and navigate when completed
                    LaunchedEffect(quizState) {
                        if (quizState is QuizUiState.QuizCompleted) {
                            currentQuizResult = (quizState as QuizUiState.QuizCompleted).result
                        }
                    }

                    QuizScreen(
                        userProfile = profile,
                        studyContent = content,
                        onQuizComplete = { passed ->
                            navController.navigate(Screen.Result.route)
                        },
                        viewModel = quizViewModel
                    )
                }
            }
        }

        // Result Screen
        composable(Screen.Result.route) {
            currentQuizResult?.let { result ->
                currentUserProfile?.let { profile ->
                    ResultScreen(
                        quizResult = result,
                        onRetryWithSimplifiedContent = {
                            // Reset quiz only (keep study content profile)
                            quizViewModel.resetViewModel()
                            // Regenerate content with increased attempt number
                            studyViewModel.regenerateContent()
                            navController.popBackStack(Screen.StudyContent.route, inclusive = false)
                        },
                        onBackToHome = { shouldReset ->
                            if (shouldReset) {
                                resetAllState()
                            }
                            navController.popBackStack(Screen.Onboarding.route, inclusive = false)
                        },
                        onNextTopic = {
                            // Reset everything for new topic
                            resetAllState()
                            navController.popBackStack(Screen.Onboarding.route, inclusive = false)
                        }
                    )
                }
            }
        }
    }
}