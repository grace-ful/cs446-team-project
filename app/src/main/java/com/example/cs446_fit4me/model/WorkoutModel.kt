package com.example.cs446_fit4me.model

import java.util.UUID

data class WorkoutModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isGeneric: Boolean? = false,
    val exercises: List<WorkoutExerciseLinkModel>
)

fun WorkoutTemplateResponse.toWorkoutModel(): WorkoutModel {
    return WorkoutModel(
        id = this.id,
        name = this.name,
        isGeneric = this.isGeneral,
        exercises = this.exercises.mapIndexed { index, ex ->
            WorkoutExerciseLinkModel(
                exerciseName = ex.name,
                sets = null, // initially blank — can be filled by user later
                reps = null,
                durationSeconds = null,
                weightKg = null,
                restSeconds = null,
                orderInWorkout = index
            )
        }
    )
}