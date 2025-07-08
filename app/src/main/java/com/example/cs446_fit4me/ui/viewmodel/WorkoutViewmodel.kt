package com.example.cs446_fit4me.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.network.ApiClient
import kotlinx.coroutines.launch
import java.util.*

class WorkoutViewModel : ViewModel() {

    private val _myWorkouts = mutableStateListOf<WorkoutModel>()
    val myWorkouts: List<WorkoutModel> get() = _myWorkouts

    private val _standardWorkouts = mutableStateListOf<WorkoutModel>()
    val standardWorkouts: List<WorkoutModel> get() = _standardWorkouts

    init {
        fetchStandardWorkouts()
    }

    private fun fetchStandardWorkouts() {
        viewModelScope.launch {
            try {
                val response = ApiClient.workoutApiService.getGeneralWorkouts()
                _standardWorkouts.clear()
                _standardWorkouts.addAll(response.map { it.toWorkoutModel() })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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
        exercises: List<ExerciseTemplate>
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
                id = UUID.randomUUID().toString(),
                name = "New Custom Workout ${_myWorkouts.size + 1}",
                isGeneric = false,
                exercises = listOf()
            )
        )
    }
}
