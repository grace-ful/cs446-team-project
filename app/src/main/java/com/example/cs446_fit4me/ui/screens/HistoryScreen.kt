package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.ui.components.*
import com.example.cs446_fit4me.ui.viewmodel.*

@Composable
fun HistoryScreen(viewModel: WorkoutSessionViewModel) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.initApi(context)
        viewModel.fetchWorkoutHistory()
        viewModel.fetchExerciseHistory()
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Workouts", "Exercises")

    val workoutHistoryState by viewModel.historyState.collectAsState()
    val exerciseHistoryState by viewModel.exerciseHistoryState.collectAsState()

    var showWorkoutDialog by remember { mutableStateOf(false) }
    var selectedWorkoutSession by remember { mutableStateOf<WorkoutSessionUI?>(null) }

    var showExerciseDialog by remember { mutableStateOf(false) }
    var selectedExerciseHistory by remember { mutableStateOf<ExerciseHistoryCardUI?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        when (selectedTab) {
            0 -> {
                when (workoutHistoryState) {
                    is WorkoutSessionHistoryState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }

                    is WorkoutSessionHistoryState.Error -> {
                        Text(
                            text = "Error: ${(workoutHistoryState as WorkoutSessionHistoryState.Error).message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    is WorkoutSessionHistoryState.Success -> {
                        val workoutSessions = (workoutHistoryState as WorkoutSessionHistoryState.Success).sessions
                        Column {
                            workoutSessions.forEach { session ->
                                WorkoutHistoryCard(
                                    workoutName = session.workoutName,
                                    workoutDate = session.workoutDate,
                                    duration = session.duration,
                                    exerciseNames = session.exerciseSessions.map { it.exerciseName },
                                    onClick = {
                                        selectedWorkoutSession = session
                                        showWorkoutDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                when (exerciseHistoryState) {
                    is ExerciseHistoryState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }

                    is ExerciseHistoryState.Error -> {
                        Text(
                            text = "Error: ${(exerciseHistoryState as ExerciseHistoryState.Error).message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    is ExerciseHistoryState.Success -> {
                        val exerciseCards = (exerciseHistoryState as ExerciseHistoryState.Success).sessions
                        Column {
                            exerciseCards.forEach { card ->
                                ExerciseHistoryCard(
                                    exerciseName = card.exerciseName,
                                    onClick = {
                                        selectedExerciseHistory = card as? ExerciseHistoryCardUI
                                        showExerciseDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showWorkoutDialog && selectedWorkoutSession != null) {
        WorkoutHistoryDialog(
            showDialog = showWorkoutDialog,
            onDismiss = { showWorkoutDialog = false },
            workoutName = selectedWorkoutSession!!.workoutName,
            workoutDate = selectedWorkoutSession!!.workoutDate,
            duration = selectedWorkoutSession!!.duration,
            exerciseSessions = selectedWorkoutSession!!.exerciseSessions
        )
    }

    if (showExerciseDialog && selectedExerciseHistory != null) {
        ExerciseDetailsDialog(
            exerciseName = selectedExerciseHistory!!.exerciseName,
            onDismiss = { showExerciseDialog = false },
            pr = selectedExerciseHistory!!.prWeight?.let {
                PRResponse(
                    id = "", // optional
                    templateId = selectedExerciseHistory!!.exerciseId,
                    userId = "",
                    weight = it,
                    duration = null,
                    reps = selectedExerciseHistory!!.recentSets
                        .filter { set -> set.weight == it }
                        .maxByOrNull { set -> set.reps }
                        ?.reps ?: 0,
                    date = selectedExerciseHistory!!.prDate ?: ""
                )
            },
            history = selectedExerciseHistory!!.recentSets.mapIndexed { index, set ->
                ExerciseSessionHistoryResponse(
                    id = "session-$index",
                    exerciseTemplate = null,
                    date = selectedExerciseHistory!!.date,
                    sets = listOf(
                        ExerciseSetResponse(
                            id = set.id ?: "set-$index",
                            reps = set.reps,
                            weight = set.weight,
                            duration = set.duration
                        )
                    )
                )
            }
        )
    }
}
