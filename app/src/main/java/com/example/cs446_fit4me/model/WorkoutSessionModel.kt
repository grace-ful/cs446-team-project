package com.example.cs446_fit4me.model

data class WorkoutSessionResponse(
    val id: String,
    val Workout: WorkoutTemplateResponse?,
    val exerciseSessions: List<ExerciseSessionResponse>
)

data class ExerciseSessionResponse(
    val id: String,
    val exerciseTemplate: ExerciseTemplate?,  // Made nullable to handle potential null values
    val sets: List<ExerciseSetResponse>
)

data class ExerciseSetResponse(
    val reps: Int,
    val weight: Float?,
    val duration: Int?,
    val isComplete: Boolean? = false
)

data class WorkoutSessionUI(
    val id: String = "",
    val workoutName: String = "",
    val exerciseSessions: List<ExerciseSessionUI> = emptyList()
)

data class ExerciseSessionUI(
    val id: String,
    val exerciseName: String,
    val sets: List<ExerciseSetUI>
)

data class ExerciseSetUI(
    val reps: Int = 0,
    val weight: Float? = null,
    val duration: Int? = null,
    val isComplete: Boolean = false
)

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
        reps = this.reps,
        weight = this.weight,
        duration = this.duration,
        isComplete = false // You control this in the UI only
    )
}