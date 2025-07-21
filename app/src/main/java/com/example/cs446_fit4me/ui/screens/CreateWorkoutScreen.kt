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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import com.example.cs446_fit4me.model.ExerciseTemplate
import com.example.cs446_fit4me.ui.viewmodel.WorkoutViewModel
import com.example.cs446_fit4me.ui.components.ExerciseListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel,
    onAddExerciseClicked: () -> Unit,
    onCreateWorkout: (String) -> Unit,
    userId: String?
) {
    val workoutName = workoutViewModel.workoutName
    val selectedExercises = workoutViewModel.selectedExercises

    val navBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(navBackStackEntry?.savedStateHandle?.get<ArrayList<ExerciseTemplate>>("selectedExercises")) {
        val returnedExercises = navBackStackEntry
            ?.savedStateHandle
            ?.get<ArrayList<ExerciseTemplate>>("selectedExercises")
        if (returnedExercises != null) {
            workoutViewModel.clearSelectedExercises()
            returnedExercises.forEach { workoutViewModel.addExercise(it) }
            navBackStackEntry.savedStateHandle.remove<ArrayList<ExerciseTemplate>>("selectedExercises")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Create New Workout", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(14.dp))

        // Workout name inside an elevated card
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            OutlinedTextField(
                value = workoutName,
                onValueChange = { workoutViewModel.updateWorkoutName(it) },
                label = { Text("Workout Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
        }

        // Add Exercise Button
        Button(
            onClick = onAddExerciseClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50)
        ) {
            Text("Add Exercise")
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            "Exercises in this workout:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
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
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedExercises) { exerciseTemplate ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp, max = 78.dp),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp),
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
                if (userId != null) {
                    workoutViewModel.createWorkoutTemplateOnServer(
                        context = context,
                        userId = userId,
                        onSuccess = { navController.popBackStack() },
                        onError = { error -> println("Failed to create workout: $error") }
                    )
                } else {
                    println("User ID is not loaded yet")
                }
            },
            enabled = workoutName.isNotBlank() && selectedExercises.isNotEmpty() && userId != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Create Workout")
        }
    }
}
