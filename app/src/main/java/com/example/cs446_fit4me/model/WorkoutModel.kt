package com.example.cs446_fit4me.model

import java.util.UUID

data class WorkoutModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isGeneric: Boolean? = false,
    val exercises: List<WorkoutExerciseLinkModel>
)

