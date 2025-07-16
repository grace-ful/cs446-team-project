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
                if (response != null) {
                    _uiState.value = response.toWorkoutSessionUI()
                    Log.d("WorkoutSessionVM", "Fetched session: ${_uiState.value}")
                } else {
                    Log.e("WorkoutSessionVM", "Response was null")
                }
            } catch (e: Exception) {
                Log.e("WorkoutSessionVM", "Failed to fetch session: ${e.message}")
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
                    exercise.copy(
                        sets = exercise.sets + ExerciseSetUI() // new blank set
                    )
                } else exercise
            }
        )
    }

    fun saveWorkoutSession(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val request = _uiState.value.toUpdateRequest()
                apiService?.updateWorkoutSession(_uiState.value.id, request)
                onSuccess()
            } catch (e: Exception) {
                Log.e("WorkoutSessionVM", "Failed to save session: ${e.message}")
            }
        }
    }

    fun deleteWorkoutSession(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                apiService?.deleteWorkoutSession(uiState.value.id)
                Log.d("WorkoutSessionVM", "Deleted session: ${uiState.value.id}")
                onSuccess()
            } catch (e: Exception) {
                Log.e("WorkoutSessionVM", "Failed to delete session: ${e.message}")
            }
        }
    }
}
