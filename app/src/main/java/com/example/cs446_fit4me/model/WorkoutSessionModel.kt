package com.example.cs446_fit4me.model

// RESPONSE MODELS (From Backend)

data class WorkoutSessionResponse(
    val id: String,
    val Workout: WorkoutTemplateResponse?,
    val exerciseSessions: List<ExerciseSessionResponse>
)

data class ExerciseSessionResponse(
    val id: String,
    val exerciseTemplate: ExerciseTemplate?, // Nullable in case template is deleted
    val sets: List<ExerciseSetResponse>
)

data class ExerciseSetResponse(
    val id: String?, // Track ID for updating
    val reps: Int,
    val weight: Float?,
    val duration: Int?,
    val isComplete: Boolean? = false
)


// UI MODELS (Frontend State)

data class WorkoutSessionUI(
    val id: String = "",
    val workoutName: String = "",
    // ISO string from backend, can be parsed & formatted
    val workoutDate: String = "",
    // in secs
    val duration: Int? = null,
    val exerciseSessions: List<ExerciseSessionUI> = emptyList()
)

data class ExerciseSessionUI(
    val id: String,
    val exerciseName: String,
    val sets: List<ExerciseSetUI>
)

data class ExerciseSetUI(
    val id: String? = null, // Needed to map updates correctly
    val reps: Int = 0,
    val weight: Float? = null,
    val duration: Int? = null,
    val isComplete: Boolean = false
)


// REQUEST MODELS (To Backend)

data class WorkoutSessionUpdateRequest(
    val exerciseSessions: List<ExerciseSessionUpdateRequest>
)

data class ExerciseSessionUpdateRequest(
    val id: String,
    val sets: List<ExerciseSetUpdateRequest>
)

data class ExerciseSetUpdateRequest(
    val id: String?, // null if it's a new set
    val reps: Int,
    val weight: Float?,
    val duration: Int? = null,
    val isCompleted: Boolean
)


// CONVERSION FUNCTIONS

fun WorkoutSessionResponse.toWorkoutSessionUI(): WorkoutSessionUI {
    return WorkoutSessionUI(
        id = this.id,
        workoutName = this.Workout?.name ?: "",
        exerciseSessions = this.exerciseSessions.map { it.toExerciseSessionUI() }
    )
}

fun ExerciseSessionResponse.toExerciseSessionUI(): ExerciseSessionUI {
    return ExerciseSessionUI(
        id = this.id,
        exerciseName = this.exerciseTemplate?.name ?: "Unknown Exercise",
        sets = this.sets.map { it.toExerciseSetUI() }
    )
}

fun ExerciseSetResponse.toExerciseSetUI(): ExerciseSetUI {
    return ExerciseSetUI(
        id = this.id,
        reps = this.reps,
        weight = this.weight,
        duration = this.duration,
        isComplete = this.isComplete ?: false
    )
}

fun WorkoutSessionUI.toUpdateRequest(): WorkoutSessionUpdateRequest {
    return WorkoutSessionUpdateRequest(
        exerciseSessions = exerciseSessions.map { ex ->
            ExerciseSessionUpdateRequest(
                id = ex.id,
                sets = ex.sets.map { set ->
                    ExerciseSetUpdateRequest(
                        id = set.id,
                        reps = set.reps,
                        weight = set.weight,
                        duration = set.duration,
                        isCompleted = set.isComplete
                    )
                }
            )
        }
    )
}
