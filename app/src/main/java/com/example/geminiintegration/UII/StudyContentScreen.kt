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
import com.example.geminiintegration.DataModels.UserProfile
import com.example.studentlearning.ui.viewmodels.StudyUiState
import com.example.studentlearning.ui.viewmodels.StudyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyContentScreen(
    userProfile: UserProfile,
    onTakeQuiz: () -> Unit,
    onBackToHome: () -> Unit,
    viewModel: StudyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState is StudyUiState.Initial) {
            viewModel.generateContent(userProfile)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study: ${userProfile.topic}") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    TextButton(onClick = onBackToHome) {
                        Text("← Home")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is StudyUiState.Initial -> {
                    // Loading will appear immediately
                }

                is StudyUiState.Loading -> {
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
                            text = "Generating personalized content...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (viewModel.getAttemptNumber() > 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Making it easier to understand (Attempt ${viewModel.getAttemptNumber()})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                is StudyUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Difficulty Badge
                        Surface(
                            color = when (state.content.difficulty) {
                                "easy" -> MaterialTheme.colorScheme.tertiaryContainer
                                "hard" -> MaterialTheme.colorScheme.errorContainer
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "Difficulty: ${state.content.difficulty.uppercase()}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        if (state.content.attemptNumber > 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "Simplified Version - Attempt ${state.content.attemptNumber}",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Study Content
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.content.content,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Take Quiz Button
                        Button(
                            onClick = onTakeQuiz,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(
                                text = "Take Quiz",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                is StudyUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.regenerateContent() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}