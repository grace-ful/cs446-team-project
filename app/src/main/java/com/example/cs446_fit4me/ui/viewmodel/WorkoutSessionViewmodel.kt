package com.example.cs446_fit4me.ui.viewmodel

import WorkoutSessionApiService
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutSessionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutSessionUI())
    val uiState: StateFlow<WorkoutSessionUI> = _uiState

    private var apiService: WorkoutSessionApiService? = null

    fun initApi(context: Context) {
        apiService = ApiClient.getWorkoutSessionApi(context)
    }

    fun fetchWorkoutSession(sessionId: String) {
        viewModelScope.launch {
            try {
                val response = apiService?.getWorkoutSession(sessionId)
                Log.d("WorkoutSessionVM", "Fetched session: $response")
                Log.d("WorkoutSessionVM", "Response type: ${response?.javaClass?.simpleName}")

                if (response != null) {
                    // Check if response is actually a WorkoutSessionResponse
                    when (response) {
                        is WorkoutSessionResponse -> {
                            _uiState.value = response.toWorkoutSessionUI()
                        }
                        else -> {
                            Log.e("WorkoutSessionVM", "Unexpected response type: ${response.javaClass}")
                            // Handle other response types or cast as needed
                        }
                    }
                } else {
                    Log.e("WorkoutSessionVM", "API returned null")
                }
            } catch (e: Exception) {
                Log.e("WorkoutSessionVM", "Failed to fetch session: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun updateReps(exerciseId: String, setIndex: Int, reps: String) {
        _uiState.value = _uiState.value.copy(
            exerciseSessions = _uiState.value.exerciseSessions.map { exercise ->
                if (exercise.id == exerciseId) {
                    exercise.copy(
                        sets = exercise.sets.toMutableList().apply {
                            this[setIndex] = this[setIndex].copy(reps = reps.toIntOrNull() ?: 0)
                        }
                    )
                } else exercise
            }
        )
    }

    fun updateWeight(exerciseId: String, setIndex: Int, weight: String) {
        _uiState.value = _uiState.value.copy(
            exerciseSessions = _uiState.value.exerciseSessions.map { exercise ->
                if (exercise.id == exerciseId) {
                    exercise.copy(
                        sets = exercise.sets.toMutableList().apply {
                            this[setIndex] = this[setIndex].copy(weight = weight.toFloatOrNull())
                        }
                    )
                } else exercise
            }
        )
    }

    fun updateCompletion(exerciseId: String, setIndex: Int, isChecked: Boolean) {
        _uiState.value = _uiState.value.copy(
            exerciseSessions = _uiState.value.exerciseSessions.map { exercise ->
                if (exercise.id == exerciseId) {
                    exercise.copy(
                        sets = exercise.sets.toMutableList().apply {
                            this[setIndex] = this[setIndex].copy(isComplete = isChecked)
                        }
                    )
                } else exercise
            }
        )
    }

    fun addSet(exerciseId: String) {
        _uiState.value = _uiState.value.copy(
            exerciseSessions = _uiState.value.exerciseSessions.map { exercise ->
                if (exercise.id == exerciseId) {
                    exercise.copy(sets = exercise.sets + ExerciseSetUI())
                } else exercise
            }
        )
    }

    fun saveWorkoutSession(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                apiService?.saveWorkoutSession(_uiState.value.id, _uiState.value)
                onSuccess()
            } catch (e: Exception) {
                Log.e("WorkoutSessionVM", "Failed to save session: ${e.message}")
            }
        }
    }
}