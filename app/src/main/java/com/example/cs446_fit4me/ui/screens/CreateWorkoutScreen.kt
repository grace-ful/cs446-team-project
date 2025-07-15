package com.example.cs446_fit4me.ui.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cs446_fit4me.model.ExerciseTemplate
import com.example.cs446_fit4me.ui.viewmodel.WorkoutViewModel
import com.example.cs446_fit4me.ui.components.ExerciseListItem

@Composable
fun CreateWorkoutScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel,
    onAddExerciseClicked: () -> Unit,
    onCreateWorkout: (String) -> Unit
) {
    val workoutName = workoutViewModel.workoutName
    val selectedExercises = workoutViewModel.selectedExercises

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Create New Workout", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = workoutName,
            onValueChange = { workoutViewModel.updateWorkoutName(it) },
            label = { Text("Workout Name") },
            placeholder = { Text("Enter a workout name...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            singleLine = true,
            shape = MaterialTheme.shapes.small
        )

        // Add Exercise Button
        Button(
            onClick = onAddExerciseClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Exercise")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Exercises in this workout:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))

        // Cards for each selected exercise
        if (selectedExercises.isEmpty()) {
            Text(
                "No exercises added yet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(selectedExercises) { exerciseTemplate ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExerciseListItem(
                                exercise = exerciseTemplate.toExercise(),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { workoutViewModel.removeExercise(exerciseTemplate.id) }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Remove"
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Create Workout button at bottom
        val context = LocalContext.current
        Button(
            onClick = {
                workoutViewModel.createWorkoutTemplateOnServer(
                    context = context,
                    onSuccess = { navController.popBackStack() },
                    onError = { error -> println("Failed to create workout: $error") }
                )
            },
            enabled = workoutName.isNotBlank() && selectedExercises.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Create Workout")
        }
    }
}
