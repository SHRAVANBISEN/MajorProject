package com.example.studentlearning.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentlearning.ui.viewmodels.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onStartLearning: (com.example.geminiintegration.DataModels.UserProfile) -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val standard by viewModel.standard.collectAsState()
    val subject by viewModel.subject.collectAsState()
    val topic by viewModel.topic.collectAsState()
    val learningLevel by viewModel.learningLevel.collectAsState()
    val additionalPrompt by viewModel.additionalPrompt.collectAsState()

    var standardExpanded by remember { mutableStateOf(false) }
    var subjectExpanded by remember { mutableStateOf(false) }
    var levelExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Learning App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Welcome! Let's personalize your learning",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Tell us about yourself to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Standard Dropdown
            ExposedDropdownMenuBox(
                expanded = standardExpanded,
                onExpandedChange = { standardExpanded = it }
            ) {
                OutlinedTextField(
                    value = standard,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Your Grade/Standard") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = standardExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = standardExpanded,
                    onDismissRequest = { standardExpanded = false }
                ) {
                    viewModel.standardOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateStandard(option)
                                standardExpanded = false
                            }
                        )
                    }
                }
            }

            // Subject Dropdown
            ExposedDropdownMenuBox(
                expanded = subjectExpanded,
                onExpandedChange = { subjectExpanded = it }
            ) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Subject") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = subjectExpanded,
                    onDismissRequest = { subjectExpanded = false }
                ) {
                    viewModel.subjectOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateSubject(option)
                                subjectExpanded = false
                            }
                        )
                    }
                }
            }

            // Topic TextField
            OutlinedTextField(
                value = topic,
                onValueChange = { viewModel.updateTopic(it) },
                label = { Text("Enter Topic") },
                placeholder = { Text("e.g., Quadratic Equations") },
                modifier = Modifier.fillMaxWidth()
            )

            // Learning Level Dropdown
            ExposedDropdownMenuBox(
                expanded = levelExpanded,
                onExpandedChange = { levelExpanded = it }
            ) {
                OutlinedTextField(
                    value = learningLevel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Your Learning Level") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = levelExpanded,
                    onDismissRequest = { levelExpanded = false }
                ) {
                    viewModel.learningLevelOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateLearningLevel(option)
                                levelExpanded = false
                            }
                        )
                    }
                }
            }

            // Additional Prompt TextField
            OutlinedTextField(
                value = additionalPrompt,
                onValueChange = { viewModel.updateAdditionalPrompt(it) },
                label = { Text("Additional Instructions (Optional)") },
                placeholder = { Text("e.g., Focus on real-world examples") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Start Button
            Button(
                onClick = {
                    if (viewModel.isFormValid()) {
                        onStartLearning(viewModel.createUserProfile())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = viewModel.isFormValid()
            ) {
                Text(
                    text = "Start Learning",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}