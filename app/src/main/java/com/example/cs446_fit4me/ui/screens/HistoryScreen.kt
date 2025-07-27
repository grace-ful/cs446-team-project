package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cs446_fit4me.ui.components.WorkoutHistoryCard
import com.example.cs446_fit4me.ui.components.ExerciseHistoryCard
import com.example.cs446_fit4me.ui.components.WorkoutHistoryDialog
import com.example.cs446_fit4me.ui.components.ExerciseDetailsDialog
import com.example.cs446_fit4me.model.WorkoutSessionUI
import com.example.cs446_fit4me.model.ExerciseSessionUI
import com.example.cs446_fit4me.model.ExerciseSetUI
import com.example.cs446_fit4me.model.ExerciseSessionHistoryResponse
import com.example.cs446_fit4me.model.PRResponse

@Composable
fun HistoryScreen(
    workoutSessions: List<WorkoutSessionUI> = sampleWorkoutSessions,
    exerciseNames: List<String> = sampleExerciseNames
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Workouts", "Exercises")

    // Dialog state for workout
    var showWorkoutDialog by remember { mutableStateOf(false) }
    var selectedSession by remember { mutableStateOf<WorkoutSessionUI?>(null) }

    // Dialog state for exercise
    var showExerciseDialog by remember { mutableStateOf(false) }
    var selectedExerciseName by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tabs at the top
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
                // Workouts Tab
                Column {
                    workoutSessions.forEach { session ->
                        WorkoutHistoryCard(
                            workoutName = session.workoutName,
                            workoutDate = session.workoutDate,
                            duration = session.duration,
                            exerciseNames = session.exerciseSessions.map { it.exerciseName },
                            onClick = {
                                selectedSession = session
                                showWorkoutDialog = true
                            }
                        )
                    }
                }
            }
            1 -> {
                // Exercises Tab
                Column {
                    exerciseNames.forEach { name ->
                        ExerciseHistoryCard(
                            exerciseName = name,
                            onClick = {
                                selectedExerciseName = name
                                showExerciseDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Workout dialog
    if (showWorkoutDialog && selectedSession != null) {
        WorkoutHistoryDialog(
            showDialog = showWorkoutDialog,
            onDismiss = { showWorkoutDialog = false },
            workoutName = selectedSession!!.workoutName,
            workoutDate = selectedSession!!.workoutDate,
            duration = selectedSession!!.duration,
            exerciseSessions = selectedSession!!.exerciseSessions
        )
    }

    // Exercise details dialog
    if (showExerciseDialog && selectedExerciseName != null) {
        val pr = sampleExercisePRs[selectedExerciseName!!]
        val history = sampleExerciseHistories[selectedExerciseName!!] ?: emptyList()
        ExerciseDetailsDialog(
            exerciseName = selectedExerciseName!!,
            onDismiss = { showExerciseDialog = false },
            pr = pr,
            history = history
        )
    }
}

// MOCK DATA

val sampleWorkoutSessions = listOf(
    WorkoutSessionUI(
        id = "1",
        workoutName = "Push Day (Custom)",
        workoutDate = "2025-07-26T10:05:00Z",
        duration = 3980,
        exerciseSessions = listOf(
            ExerciseSessionUI(
                id = "ex1",
                exerciseName = "Bench Press",
                sets = listOf(
                    ExerciseSetUI(id = "s1", reps = 12, weight = 45f),
                    ExerciseSetUI(id = "s2", reps = 10, weight = 50f),
                    ExerciseSetUI(id = "s3", reps = 8,  weight = 60f)
                )
            ),
            ExerciseSessionUI(
                id = "ex2",
                exerciseName = "Overhead Press",
                sets = listOf(
                    ExerciseSetUI(id = "s4", reps = 10, weight = 25f),
                    ExerciseSetUI(id = "s5", reps = 8,  weight = 30f)
                )
            ),
            ExerciseSessionUI(
                id = "ex3",
                exerciseName = "Triceps Dips",
                sets = listOf(
                    ExerciseSetUI(id = "s6", reps = 15, weight = null),
                    ExerciseSetUI(id = "s7", reps = 12, weight = null)
                )
            ),
            ExerciseSessionUI(
                id = "ex4",
                exerciseName = "Chest Fly",
                sets = listOf(
                    ExerciseSetUI(id = "s8", reps = 12, weight = 20f),
                    ExerciseSetUI(id = "s9", reps = 10, weight = 22.5f)
                )
            ),
            ExerciseSessionUI(
                id = "ex5",
                exerciseName = "Incline Dumbbell Press",
                sets = listOf(
                    ExerciseSetUI(id = "s10", reps = 12, weight = 25f),
                    ExerciseSetUI(id = "s11", reps = 10, weight = 25f),
                    ExerciseSetUI(id = "s12", reps = 8, weight = 27.5f)
                )
            ),
            ExerciseSessionUI(
                id = "ex6",
                exerciseName = "Push-Ups",
                sets = listOf(
                    ExerciseSetUI(id = "s13", reps = 20, weight = null),
                    ExerciseSetUI(id = "s14", reps = 18, weight = null)
                )
            ),
            ExerciseSessionUI(
                id = "ex7",
                exerciseName = "Shoulder Press Machine",
                sets = listOf(
                    ExerciseSetUI(id = "s15", reps = 12, weight = 30f),
                    ExerciseSetUI(id = "s16", reps = 10, weight = 35f)
                )
            ),
            ExerciseSessionUI(
                id = "ex8",
                exerciseName = "Cable Triceps Pushdown",
                sets = listOf(
                    ExerciseSetUI(id = "s17", reps = 15, weight = 20f),
                    ExerciseSetUI(id = "s18", reps = 12, weight = 22.5f),
                    ExerciseSetUI(id = "s19", reps = 10, weight = 25f)
                )
            ),
            ExerciseSessionUI(
                id = "ex9",
                exerciseName = "Pec Deck",
                sets = listOf(
                    ExerciseSetUI(id = "s20", reps = 12, weight = 30f),
                    ExerciseSetUI(id = "s21", reps = 10, weight = 32.5f)
                )
            ),
            ExerciseSessionUI(
                id = "ex10",
                exerciseName = "Diamond Push-Ups",
                sets = listOf(
                    ExerciseSetUI(id = "s22", reps = 15, weight = null),
                    ExerciseSetUI(id = "s23", reps = 12, weight = null)
                )
            ),
            ExerciseSessionUI(
                id = "ex11",
                exerciseName = "Arnold Press",
                sets = listOf(
                    ExerciseSetUI(id = "s24", reps = 10, weight = 22.5f),
                    ExerciseSetUI(id = "s25", reps = 8, weight = 25f)
                )
            ),
            ExerciseSessionUI(
                id = "ex12",
                exerciseName = "Dumbbell Lateral Raise",
                sets = listOf(
                    ExerciseSetUI(id = "s26", reps = 15, weight = 10f),
                    ExerciseSetUI(id = "s27", reps = 15, weight = 12.5f)
                )
            )
        )
    ),
    WorkoutSessionUI(
        id = "2",
        workoutName = "Pull Day (General)",
        workoutDate = "2025-07-24T11:20:00Z",
        duration = 3620,
        exerciseSessions = listOf(
            ExerciseSessionUI(
                id = "ex5",
                exerciseName = "Lat Pulldown",
                sets = listOf(
                    ExerciseSetUI(id = "s28", reps = 12, weight = 40f),
                    ExerciseSetUI(id = "s29", reps = 10, weight = 45f)
                )
            ),
            ExerciseSessionUI(
                id = "ex6",
                exerciseName = "Seated Row",
                sets = listOf(
                    ExerciseSetUI(id = "s30", reps = 12, weight = 35f),
                    ExerciseSetUI(id = "s31", reps = 10, weight = 37.5f)
                )
            )
        )
    )
)

val sampleExerciseNames = listOf(
    "Bench Press (Big name please okay)", "Overhead Press", "Lat Pulldown", "Squat", "Deadlift"
)

// Map of exerciseName -> PRResponse (for ExerciseDetailsDialog)
val sampleExercisePRs = mapOf(
    "Bench Press (Big name please okay)" to PRResponse(
        id = "pr1",
        templateId = "ex1",
        userId = "user1",
        weight = 60f,
        duration = 10, // reps
        date = "2025-07-15T09:00:00Z"
    ),
    "Overhead Press" to PRResponse(
        id = "pr2",
        templateId = "ex2",
        userId = "user1",
        weight = 40f,
        duration = 8,
        date = "2025-07-18T14:30:00Z"
    )
)

val sampleExerciseHistories = mapOf(
    "Bench Press (Big name please okay)" to listOf(
        ExerciseSessionHistoryResponse(
            id = "h1",
            exerciseTemplate = null,
            date = "2025-07-20T10:15:00Z",
            sets = listOf(
                com.example.cs446_fit4me.model.ExerciseSetResponse(id = "s1", reps = 12, weight = 45f, duration = null),
                com.example.cs446_fit4me.model.ExerciseSetResponse(id = "s2", reps = 10, weight = 50f, duration = null),
                com.example.cs446_fit4me.model.ExerciseSetResponse(id = "s3", reps = 8,  weight = 60f, duration = null)
            )
        ),
        ExerciseSessionHistoryResponse(
            id = "h2",
            exerciseTemplate = null,
            date = "2025-07-15T09:00:00Z",
            sets = listOf(
                com.example.cs446_fit4me.model.ExerciseSetResponse(id = "s4", reps = 10, weight = 55f, duration = null)
            )
        )
    ),
    "Overhead Press" to listOf(
        ExerciseSessionHistoryResponse(
            id = "h3",
            exerciseTemplate = null,
            date = "2025-07-18T14:30:00Z",
            sets = listOf(
                com.example.cs446_fit4me.model.ExerciseSetResponse(id = "s5", reps = 8, weight = 40f, duration = null)
            )
        )
    )
    // ...add more if you want
)
