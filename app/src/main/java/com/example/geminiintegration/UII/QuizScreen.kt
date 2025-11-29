package com.example.studentlearning.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geminiintegration.DataModels.QuizQuestion
import com.example.geminiintegration.DataModels.StudyContent
import com.example.geminiintegration.DataModels.UserProfile

import com.example.studentlearning.ui.viewmodels.QuizUiState
import com.example.studentlearning.ui.viewmodels.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    userProfile: UserProfile,
    studyContent: StudyContent,
    onQuizComplete: (Boolean) -> Unit, // Pass whether quiz was passed
    viewModel: QuizViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState is QuizUiState.Initial) {
            viewModel.generateQuiz(userProfile, studyContent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz: ${userProfile.topic}") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is QuizUiState.Initial -> {
                    // Will show loading immediately
                }

                is QuizUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Generating quiz questions...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                is QuizUiState.QuizReady -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Answer all ${state.quiz.questions.size} questions",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        state.quiz.questions.forEachIndexed { index, question ->
                            QuizQuestionCard(
                                questionNumber = index + 1,
                                question = question,
                                onAnswerSelected = { answerIndex ->
                                    viewModel.selectAnswer(index, answerIndex)
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Button(
                            onClick = { viewModel.submitQuiz() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(
                                text = "Submit Quiz",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                is QuizUiState.QuizCompleted -> {
                    LaunchedEffect(state.result) {
                        onQuizComplete(state.result.passed)
                    }
                }

                is QuizUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.generateQuiz(userProfile, studyContent) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizQuestionCard(
    questionNumber: Int,
    question: QuizQuestion,
    onAnswerSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Question $questionNumber",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            question.options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = question.selectedAnswerIndex == index,
                        onClick = { onAnswerSelected(index) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}