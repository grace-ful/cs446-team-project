package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs446_fit4me.model.ExerciseSetUI
import com.example.cs446_fit4me.ui.viewmodel.WorkoutSessionViewModel

@Composable
fun WorkoutSessionScreen(
    sessionId: String,
    navController: NavController,
    viewModel: WorkoutSessionViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val sessionDeleted by viewModel.sessionDeleted.collectAsState()
    var showQuitDialog by remember { mutableStateOf(false) }
    val isSaveEnabled by remember(uiState.exerciseSessions) {
        derivedStateOf {
            uiState.exerciseSessions.any { ex ->
                ex.sets.any { it.isComplete }
            }
        }
    }

    // Pause state
    var isPaused by remember { mutableStateOf(false) }

    LaunchedEffect(sessionId) {
        viewModel.resetSessionDeleted()
        viewModel.resetTimer()
        viewModel.startTimer()
        isPaused = false
    }

    LaunchedEffect(Unit) {
        viewModel.initApi(context)
        viewModel.fetchWorkoutSession(sessionId)
    }

    LaunchedEffect(sessionDeleted) {
        if (sessionDeleted) {
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    if (uiState.workoutName.isBlank()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- Banner Header ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, shape = MaterialTheme.shapes.extraLarge)
                .padding(bottom = 14.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏋️  ${uiState.workoutName}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1
                )
                Spacer(Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🕒 ", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = viewModel.elapsedTime.value,
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (isPaused) {
                                viewModel.resumeTimer()
                            } else {
                                viewModel.pauseTimer()
                            }
                            isPaused = !isPaused
                        }
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Resume Timer" else "Pause Timer"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.exerciseSessions.isEmpty()) {
            Text("No exercises found in this session.")
        } else {
            val expandedMap = remember(uiState.exerciseSessions) {
                mutableStateMapOf<String, Boolean>().apply {
                    uiState.exerciseSessions.forEach { exercise ->
                        this[exercise.id] = exercise.sets.isNotEmpty()
                    }
                }
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.exerciseSessions) { exercise ->
                    val exerciseId = exercise.id
                    val expanded = expandedMap.getOrDefault(exerciseId, false)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Top row: dropdown toggle, name, set count, add button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedMap[exerciseId] = !expanded }
                            ) {
                                Icon(
                                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (expanded) "Collapse" else "Expand"
                                )

                                Text(
                                    text = "${exercise.exerciseName} (${exercise.sets.size} Sets)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                )

                                TextButton(
                                    onClick = {
                                        viewModel.addSet(exerciseId)
                                        expandedMap[exerciseId] = true // Auto-expand on add
                                    }
                                ) {
                                    Text("+ Add Set")
                                }
                            }

                            // Set list
                            if (expanded) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                exercise.sets.forEachIndexed { index, set ->
                                    SetRow(
                                        set = set,
                                        setNumber = index + 1,
                                        onRepsChange = { viewModel.updateReps(exerciseId, index, it) },
                                        onWeightChange = { viewModel.updateWeight(exerciseId, index, it) },
                                        onCompletionToggle = { viewModel.updateCompletion(exerciseId, index, it) },
                                        onDelete = {
                                            viewModel.removeSet(exerciseId, index) {
                                                expandedMap[exerciseId] = false // Collapse if no sets left
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { showQuitDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Quit")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    val durationMillis = viewModel.stopTimer() ?: 0L
                    viewModel.saveWorkoutSession(durationMillis) {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = isSaveEnabled
            ) {
                Text("Save Workout")
            }

        }
    }

    // Confirmation dialog for quitting
    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.stopTimer()
                    viewModel.resetTimer()
                    viewModel.deleteWorkoutSession { viewModel.setSessionDeleted() }
                    showQuitDialog = false
                }) { Text("Quit Workout") }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) { Text("Cancel") }
            },
            title = { Text("Quit Workout?") },
            text = { Text("Are you sure you want to quit this workout? Your progress and any unsaved changes will be lost.") }
        )
    }
}

@Composable
fun SetRow(
    set: ExerciseSetUI,
    setNumber: Int,
    onRepsChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onCompletionToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var repsText by remember { mutableStateOf(if (set.reps > 0) set.reps.toString() else "") }
    var weightText by remember { mutableStateOf(set.weight?.takeIf { it > 0 }?.toString() ?: "") }

    // Checkbox enabled if both fields are non-empty and numeric
    val isCheckboxEnabled by remember(repsText, weightText) {
        derivedStateOf {
            repsText.isNotBlank() && repsText.all { it.isDigit() } &&
                    weightText.isNotBlank() && weightText.toDoubleOrNull() != null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (set.isComplete) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text("Set $setNumber", modifier = Modifier.width(60.dp))

            // Reps Field
            OutlinedTextField(
                value = repsText,
                onValueChange = {
                    repsText = it.filter { char -> char.isDigit() } // Only digits
                    onRepsChange(repsText.ifBlank { "0" })
                },
                label = { Text("Reps") },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                singleLine = true
            )

            // Weight Field
            OutlinedTextField(
                value = weightText,
                onValueChange = {
                    weightText = it.filterIndexed { index, c ->
                        c.isDigit() || (c == '.' && !it.take(index).contains('.'))
                    }
                    onWeightChange(weightText.ifBlank { "0" })
                },
                label = { Text("Weight") },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                singleLine = true
            )

            LaunchedEffect(isCheckboxEnabled) {
                if (!isCheckboxEnabled && set.isComplete) {
                    onCompletionToggle(false)
                }
            }

            // Checkbox (enabled only if valid input)
            Checkbox(
                checked = set.isComplete,
                onCheckedChange = onCompletionToggle,
                enabled = isCheckboxEnabled
            )

            // Delete Set
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Set",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
