package com.example.cs446_fit4me.ui.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cs446_fit4me.model.ExerciseTemplate
import com.example.cs446_fit4me.ui.viewmodel.WorkoutViewModel

@Composable
fun CreateWorkoutScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel,
    onAddExerciseClicked: () -> Unit,
    onCreateWorkout: (String) -> Unit
) {
    val workoutName = workoutViewModel.workoutName

    // ✅ Automatically recomposes when exercises are added/removed
    val selectedExercises = workoutViewModel.selectedExercises

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Create New Workout", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = workoutName,
            onValueChange = { workoutViewModel.updateWorkoutName(it) },
            label = { Text("Workout Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddExerciseClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Exercise")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Exercises in this workout:")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(selectedExercises) { exercise ->
                Text("• ${exercise.name}")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        val context = LocalContext.current
        Button(
            onClick = {
                    workoutViewModel.createWorkoutTemplateOnServer(
                        context = context,

                        onSuccess = {
                            navController.popBackStack()
                        },
                        onError = { error ->
                            println("Failed to create workout: $error")
                        }
                    )
            },
            enabled = workoutName.isNotBlank() && selectedExercises.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Workout")
        }
    }
}
