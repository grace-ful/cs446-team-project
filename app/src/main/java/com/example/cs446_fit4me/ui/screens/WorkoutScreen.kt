// File: ui/screens/WorkoutScreen.kt

package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.ui.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val myWorkouts by remember { derivedStateOf { workoutViewModel.myWorkouts } }


    var standardWorkouts by remember { mutableStateOf<List<WorkoutModel>>(emptyList()) }
    var standardWorkoutsLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.workoutApiService.getGeneralWorkouts()
            println("Standard workouts1: $response")
            standardWorkouts = response.map { it.toWorkoutModel() }
            println("Standard workouts2: $standardWorkouts")
            standardWorkoutsLoaded = true
        } catch (e: Exception) {
            println("Failed to load standard workouts: ${e.message}")
        }
    }

    var selectedMyWorkoutName by remember { mutableStateOf<String?>(null) }
    var selectedStandardWorkoutName by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val hasSelection = selectedMyWorkoutName != null || selectedStandardWorkoutName != null

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (hasSelection) {
                        showConfirmDialog = true
                    } else {
                        workoutViewModel.addMockWorkout()
                    }
                },
                containerColor = if (hasSelection) MaterialTheme.colorScheme.error else Color(0xFF007AFF)
            ) {
                Icon(
                    imageVector = if (hasSelection) Icons.Default.Delete else Icons.Default.Add,
                    contentDescription = if (hasSelection) "Delete Workout" else "Add Workout",
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding() + 80.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                CombinedWorkoutSection(
                    myWorkouts = myWorkouts,
                    standardWorkouts = standardWorkouts,
                    selectedMyWorkoutName = selectedMyWorkoutName,
                    selectedStandardWorkoutName = selectedStandardWorkoutName,
                    onMyWorkoutLongPress = { selectedMyWorkoutName = it; selectedStandardWorkoutName = null },
                    onStandardWorkoutLongPress = { selectedStandardWorkoutName = it; selectedMyWorkoutName = null },
                    onMyWorkoutDeselect = { selectedMyWorkoutName = null },
                    onStandardWorkoutDeselect = { selectedStandardWorkoutName = null }
                )
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedMyWorkoutName?.let {
                        workoutViewModel.deleteWorkout(it)
                        selectedMyWorkoutName = null
                    }
                    selectedStandardWorkoutName?.let {
                        standardWorkouts.removeIf { workout -> workout.name == it }
                        selectedStandardWorkoutName = null
                    }
                    showConfirmDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedMyWorkoutName = null
                    selectedStandardWorkoutName = null
                    showConfirmDialog = false
                }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Workout?") },
            text = { Text("Are you sure you want to delete this workout? This cannot be undone.") }
        )
    }
}

@Composable
fun CombinedWorkoutSection(
    myWorkouts: List<WorkoutModel>,
    standardWorkouts: List<WorkoutModel>,
    selectedMyWorkoutName: String? = null,
    selectedStandardWorkoutName: String? = null,
    onMyWorkoutLongPress: (String) -> Unit = {},
    onStandardWorkoutLongPress: (String) -> Unit = {},
    onMyWorkoutDeselect: () -> Unit = {},
    onStandardWorkoutDeselect: () -> Unit = {}
) {
    // My Exercises Section
    val myLabel = if (myWorkouts.size <= 1) "My Workout (${myWorkouts.size})" else "My Workouts (${myWorkouts.size})"
    Text(myLabel, style = MaterialTheme.typography.titleMedium)

    Spacer(modifier = Modifier.height(8.dp))

    if (myWorkouts.isEmpty()) {
        Text(
            "No workouts yet. Tap + to add one.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    } else {
        // Display my workouts in a grid layout
        myWorkouts.chunked(2).forEach { rowWorkouts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowWorkouts.forEach { workout ->
                    val isSelected = workout.name == selectedMyWorkoutName
                    WorkoutCard(
                        title = workout.name,
                        exercises = workout.exercises.joinToString(", ") { it.exerciseName },
                        isSelected = isSelected,
                        onLongPress = {
                            if (isSelected) onMyWorkoutDeselect() else onMyWorkoutLongPress(workout.name)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if odd number of items
                if (rowWorkouts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Standard Exercises Section
    val standardLabel = "Standard Workouts (${standardWorkouts.size})" //if (standardWorkouts.size <= 1) "Standard Workout (1)" else "Standard Workouts (${standardWorkouts.size})"
    Text(standardLabel, style = MaterialTheme.typography.titleMedium)

    Spacer(modifier = Modifier.height(8.dp))

    if (standardWorkouts.isEmpty()) {
        Text(
            "No standard workouts available.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    } else {
        // Display standard workouts in a grid layout
        standardWorkouts.chunked(2).forEach { rowWorkouts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowWorkouts.forEach { workout ->
                    val isSelected = workout.name == selectedStandardWorkoutName
                    WorkoutCard(
                        title = workout.name,
                        exercises = workout.exercises.joinToString(", ") { it.exerciseName },
                        isSelected = isSelected,
                        onLongPress = {
                            if (isSelected) onStandardWorkoutDeselect() else onStandardWorkoutLongPress(workout.name)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if odd number of items
                if (rowWorkouts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkoutCard(
    title: String,
    exercises: String,
    timeAgo: String? = null,
    isSelected: Boolean = false,
    onLongPress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(vertical = 6.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = onLongPress
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                exercises,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            timeAgo?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(it, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}