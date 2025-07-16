package com.example.cs446_fit4me.ui.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs446_fit4me.datastore.UserPreferencesManager
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.ui.components.WorkoutPreviewDialog
import com.example.cs446_fit4me.ui.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = viewModel(),
    onEditWorkout: (WorkoutModel) -> Unit
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesManager(context) }
    val userId by userPrefs.userIdFlow.collectAsState(initial = null)

    val standardWorkouts = workoutViewModel.standardWorkouts
    val myWorkouts by remember { derivedStateOf { workoutViewModel.myWorkouts } }

    var selectedMyWorkoutName by remember { mutableStateOf<String?>(null) }
    var selectedStandardWorkoutName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        workoutViewModel.fetchStandardWorkouts(context)
        workoutViewModel.fetchUserWorkouts(context)
    }

    var previewWorkout by remember { mutableStateOf<WorkoutModel?>(null) }
    var previewIsCustom by remember { mutableStateOf(false) }

    val navBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(navBackStackEntry?.savedStateHandle?.get<ArrayList<ExerciseTemplate>>("selectedExercises")) {
        val returnedExercises = navBackStackEntry
            ?.savedStateHandle
            ?.get<ArrayList<ExerciseTemplate>>("selectedExercises")
        if (returnedExercises != null && previewWorkout != null) {
            previewWorkout = previewWorkout!!.copy(exercises = returnedExercises)
            navBackStackEntry.savedStateHandle.remove<ArrayList<ExerciseTemplate>>("selectedExercises")
        }
    }

    if (previewWorkout != null) {
        WorkoutPreviewDialog(
            workout = previewWorkout!!,
            isCustom = previewIsCustom,
            onClose = { previewWorkout = null },
            onStart = {
                previewWorkout?.let { w ->
                    workoutViewModel.startWorkoutSessionFromTemplate(
                        context = context,
                        templateId = w.id,
                        onSuccess = { sessionId ->
                            previewWorkout = null
                            navController.navigate("workout_session/$sessionId")
                        },
                        onError = { error ->
                            Log.e("WorkoutScreen", "Failed to start session: $error")
                        }
                    )
                }
            },
            onDelete = {
                previewWorkout?.let { w ->
                    if (previewIsCustom) {
                        workoutViewModel.deleteWorkout(w.name)
                    }
                }
                previewWorkout = null
            },
            onEdit = {
                val workout = previewWorkout!!
                previewWorkout = null
                onEditWorkout(workout)
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_workout") },
                containerColor = Color(0xFF007AFF)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Workout",
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
                    onWorkoutClick = { workout ->
                        previewWorkout = workout
                        previewIsCustom = !workout.isGeneric
                    },
                    selectedMyWorkoutName = selectedMyWorkoutName,
                    selectedStandardWorkoutName = selectedStandardWorkoutName,
                    onMyWorkoutLongPress = {
                        selectedMyWorkoutName = it
                        selectedStandardWorkoutName = null
                    },
                    onStandardWorkoutLongPress = {
                        selectedStandardWorkoutName = it
                        selectedMyWorkoutName = null
                    },
                    onMyWorkoutDeselect = { selectedMyWorkoutName = null },
                    onStandardWorkoutDeselect = { selectedStandardWorkoutName = null },
                    onStartWorkoutClicked = { templateId ->
                        workoutViewModel.startWorkoutSessionFromTemplate(
                            context = context,
                            templateId = templateId,
                            onSuccess = { sessionId ->
                                navController.navigate("workout_session/$sessionId")
                            },
                            onError = { error -> println("Failed to start session: $error") }
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun CombinedWorkoutSection(
    myWorkouts: List<WorkoutModel>,
    standardWorkouts: List<WorkoutModel>,
    onWorkoutClick: (WorkoutModel) -> Unit,
    selectedMyWorkoutName: String? = null,
    selectedStandardWorkoutName: String? = null,
    onMyWorkoutLongPress: (String) -> Unit = {},
    onStandardWorkoutLongPress: (String) -> Unit = {},
    onMyWorkoutDeselect: () -> Unit = {},
    onStandardWorkoutDeselect: () -> Unit = {},
    onStartWorkoutClicked: (String) -> Unit = {}
) {
    val context = LocalContext.current

    val myLabel = if (myWorkouts.size == 1) "My Workout (1)" else "My Workouts (${myWorkouts.size})"
    Text(myLabel, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    if (myWorkouts.isEmpty()) {
        Text(
            "No workouts yet. Tap + to add one.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    } else {
        myWorkouts.chunked(2).forEach { rowWorkouts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowWorkouts.forEach { workout ->
                    WorkoutCard(
                        title = workout.name,
                        exercises = workout.exercises.joinToString(", ") { it.name },
                        onClick = { onWorkoutClick(workout) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowWorkouts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    val standardLabel = "Standard Workouts (${standardWorkouts.size})"
    Text(standardLabel, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    if (standardWorkouts.isEmpty()) {
        Text(
            "No standard workouts available.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    } else {
        standardWorkouts.chunked(2).forEach { rowWorkouts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowWorkouts.forEach { workout ->
                    WorkoutCard(
                        title = workout.name,
                        exercises = workout.exercises.joinToString(", ") { it.name },
                        onClick = { onWorkoutClick(workout) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowWorkouts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutCard(
    title: String,
    exercises: String,
    timeAgo: String? = null,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .border(2.dp, Color.Transparent, RoundedCornerShape(12.dp))
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exercises,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            timeAgo?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
