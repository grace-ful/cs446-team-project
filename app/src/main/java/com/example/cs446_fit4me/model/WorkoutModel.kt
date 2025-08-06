package com.example.cs446_fit4me.model

import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

data class WorkoutModel(
    val id: String,
    val name: String,
    val isGeneric: Boolean = false,
    val exercises: List<ExerciseTemplate>
)

fun WorkoutTemplateResponse.toWorkoutModel(): WorkoutModel {
    return WorkoutModel(
        id = this.id,
        name = this.name,
        isGeneric = this.isGeneral,
        exercises = this.exercises
    )
}

data class CreateWorkoutTemplateRequest(
    val name: String,
    val exerciseIds: List<String>,
    val isGeneral: Boolean = false,
    val userId: String,
)