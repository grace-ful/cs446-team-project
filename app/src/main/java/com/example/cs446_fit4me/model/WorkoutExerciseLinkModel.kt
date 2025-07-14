package com.example.cs446_fit4me.model

data class WorkoutExerciseLinkModel(
    val exerciseName: String,
    val sets: Int? = null,
    val reps: Int? = null,
    val durationSeconds: Int? = null,
    val weightKg: Double? = null,
    val restSeconds: Int? = null,
    val orderInWorkout: Int
)

