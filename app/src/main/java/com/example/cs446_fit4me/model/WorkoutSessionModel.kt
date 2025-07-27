package com.example.cs446_fit4me.model

// ======================
// RESPONSE MODELS (From Backend)
// ======================

data class WorkoutSessionResponse(
    val id: String,
    val Workout: WorkoutTemplateResponse?,      // may be null if deleted
    val workoutDate: String?,                   // ISO date string or null
    val duration: Int?,                         // in seconds or null
    val exerciseSessions: List<ExerciseSessionResponse>? // may be null
)

data class ExerciseSessionResponse(
    val id: String,
    val exerciseTemplate: ExerciseTemplate?, // Nullable in case template is deleted
    val sets: List<ExerciseSetResponse>?,
    val date: String // ✅ Add this line
)


data class ExerciseSetResponse(
    val id: String?,         // nullable for new sets
    val reps: Int,
    val weight: Float?,
    val duration: Int?,
    val isComplete: Boolean? = false
)


// ======================
// UI MODELS (Frontend State)
// ======================

data class WorkoutSessionUI(
    val id: String = "",
    val workoutName: String = "",
    val workoutDate: String = "",
    val duration: Int? = null,
    val exerciseSessions: List<ExerciseSessionUI> = emptyList()
)

data class ExerciseSessionUI(
    val id: String,
    val exerciseName: String,
    val sets: List<ExerciseSetUI>
)

data class ExerciseSetUI(
    val id: String? = null,
    val reps: Int = 0,
    val weight: Float? = null,
    val duration: Int? = null,
    val isComplete: Boolean = false
)


// ======================
// REQUEST MODELS (To Backend)
// ======================

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


// ======================
// CONVERSION FUNCTIONS
// ======================

fun WorkoutSessionResponse.toWorkoutSessionUI(): WorkoutSessionUI {
    return WorkoutSessionUI(
        id = this.id,
        workoutName = this.Workout?.name ?: "Unnamed Workout",
        workoutDate = this.workoutDate ?: "",
        duration = this.duration ?: 0,
        exerciseSessions = this.exerciseSessions
            ?.filterNotNull()
            ?.map { it.toExerciseSessionUI() }
            ?: emptyList()
    )
}

fun ExerciseSessionResponse.toExerciseSessionUI(): ExerciseSessionUI {
    val name = this.exerciseTemplate?.name ?: "Deleted Exercise"
    val safeSets = this.sets
        ?.filterNotNull()
        ?.map { it.toExerciseSetUI() }
        ?: emptyList()

    return ExerciseSessionUI(
        id = this.id,
        exerciseName = name,
        sets = safeSets
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
        exerciseSessions = this.exerciseSessions.map { ex ->
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
