package com.example.cs446_fit4me.ui.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cs446_fit4me.model.ExerciseSetUI
import com.example.cs446_fit4me.ui.viewmodel.WorkoutSessionViewModel

@Composable
fun WorkoutSessionScreen(
    sessionId: String,
    navController: NavController,
    viewModel: WorkoutSessionViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initApi(context)
        viewModel.fetchWorkoutSession(sessionId)
    }

    if (uiState.workoutName.isBlank()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Text(text = uiState.workoutName, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(18.dp))

        if (uiState.exerciseSessions.isEmpty()) {
            Text("No exercises found in this session.")
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.exerciseSessions) { exercise ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = exercise.exerciseName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            exercise.sets.forEachIndexed { index, set ->
                                SetRow(
                                    set = set,
                                    setNumber = index + 1,
                                    onRepsChange = { viewModel.updateReps(exercise.id, index, it) },
                                    onWeightChange = { viewModel.updateWeight(exercise.id, index, it) },
                                    onCompletionToggle = { viewModel.updateCompletion(exercise.id, index, it) }
                                )
                            }

                            OutlinedButton(
                                onClick = { viewModel.addSet(exercise.id) },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 12.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Set")
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
                onClick = {
                    viewModel.deleteWorkoutSession {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Quit")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    viewModel.saveWorkoutSession {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Workout")
            }
        }
    }
}

@Composable
fun SetRow(
    set: ExerciseSetUI,
    setNumber: Int,
    onRepsChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onCompletionToggle: (Boolean) -> Unit
) {
    var repsText by remember { mutableStateOf(if (set.reps > 0) set.reps.toString() else "") }
    var weightText by remember { mutableStateOf(set.weight?.takeIf { it > 0 }?.toString() ?: "") }

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
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text("Set $setNumber", modifier = Modifier.width(60.dp))

            OutlinedTextField(
                value = repsText,
                onValueChange = {
                    repsText = it.filter { char -> char.isDigit() }  // Only digits
                    onRepsChange(repsText.ifBlank { "0" }) // fallback to 0
                },
                label = { Text("Reps") },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                singleLine = true
            )

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

            Checkbox(
                checked = set.isComplete,
                onCheckedChange = onCompletionToggle
            )
        }
    }
}
