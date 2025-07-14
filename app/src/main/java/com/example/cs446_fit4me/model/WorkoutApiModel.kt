package com.example.cs446_fit4me.model

data class WorkoutTemplateResponse(
    val id: String,
    val name: String,
    val isGeneral: Boolean,
    val userId: String?,
    val exercises: List<ExerciseTemplate>,
    val createdAt: String
)

data class ExerciseIdListRequest(
    val exerciseIds: List<String>
)

data class RemoveExerciseRequest(
    val exerciseId: String
)
