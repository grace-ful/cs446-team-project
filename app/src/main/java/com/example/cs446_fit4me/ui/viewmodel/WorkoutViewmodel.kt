package com.example.cs446_fit4me.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.cs446_fit4me.model.WorkoutExerciseLinkModel
import com.example.cs446_fit4me.model.WorkoutModel
import java.util.*

class WorkoutViewModel : ViewModel() {

    private val _myWorkouts = mutableStateListOf<WorkoutModel>()
    val myWorkouts: List<WorkoutModel> get() = _myWorkouts

    val standardWorkouts = listOf(
        WorkoutModel(
            id = UUID.randomUUID().toString(),
            name = "Strong 5×5 - Workout B",
            isGeneric = true,
            exercises = listOf(
                WorkoutExerciseLinkModel("Squat", orderInWorkout = 1),
                WorkoutExerciseLinkModel("Overhead Press", orderInWorkout = 2),
                WorkoutExerciseLinkModel("Deadlift", orderInWorkout = 3)
            )
        ),
        WorkoutModel(
            id = UUID.randomUUID().toString(),
            name = "Legs",
            isGeneric = true,
            exercises = listOf(
                WorkoutExerciseLinkModel("Squat", orderInWorkout = 1),
                WorkoutExerciseLinkModel("Leg Extension", orderInWorkout = 2),
                WorkoutExerciseLinkModel("Flat Leg Raise", orderInWorkout = 3)
            )
        ),
        WorkoutModel(
            id = UUID.randomUUID().toString(),
            name = "Chest and Triceps",
            isGeneric = true,
            exercises = listOf(
                WorkoutExerciseLinkModel("Bench Press", orderInWorkout = 1),
                WorkoutExerciseLinkModel("Incline Press", orderInWorkout = 2),
                WorkoutExerciseLinkModel("Military Press", orderInWorkout = 3)
            )
        )
    )

    fun createEmptyWorkout(name: String = "Untitled"): String {
        val id = UUID.randomUUID().toString()
        _myWorkouts.add(
            WorkoutModel(
                id = id,
                name = name,
                isGeneric = false,
                exercises = emptyList()
            )
        )
        return id
    }

    fun addExercisesToWorkout(
        workoutId: String,
        exercises: List<WorkoutExerciseLinkModel>
    ) {
        val index = _myWorkouts.indexOfFirst { it.id == workoutId }
        if (index != -1) {
            val old = _myWorkouts[index]
            _myWorkouts[index] = old.copy(exercises = exercises)
        }
    }

    fun getWorkoutById(name: String): WorkoutModel? {
        return _myWorkouts.find { it.name == name }
    }

    fun deleteWorkout(name: String) {
        _myWorkouts.removeIf { it.name == name }
    }

    fun addWorkout(workout: WorkoutModel) {
        _myWorkouts.add(workout)
    }

    fun addMockWorkout() {
        if (_myWorkouts.size >= 9) return // current bypass to bug when adding more than 10 workouts
        _myWorkouts.add(
            WorkoutModel(
                name = "New Custom Workout ${_myWorkouts.size + 1}",
                isGeneric = false,
                exercises = listOf()
            )
        )
    }
}
