package com.example.cs446_fit4me.ui.viewmodel

import WorkoutSessionApiService
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.network.ApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WorkoutSessionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutSessionUI())
    val uiState: StateFlow<WorkoutSessionUI> = _uiState

    private var apiService: WorkoutSessionApiService? = null

    private val _sessionDeleted = MutableStateFlow(false)
    val sessionDeleted: StateFlow<Boolean> = _sessionDeleted

    // Timer logic
    private var sessionStartTime: Long? = null
    private var timerJob: Job? = null

    private val _elapsedTime = mutableStateOf("00:00:00")
    val elapsedTime: State<String> get() = _elapsedTime

    fun startTimer() {
        sessionStartTime = System.currentTimeMillis()
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                sessionStartTime?.let {
                    val elapsed = System.currentTimeMillis() - it
                    _elapsedTime.value = formatElapsedTime(elapsed)
                }
            }
        }
    }

    fun stopTimer(): Long? {
        timerJob?.cancel()
        timerJob = null
        val elapsed = sessionStartTime?.let { System.currentTimeMillis() - it }
        sessionStartTime = null
        return elapsed
    }

    private fun formatElapsedTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return "%d:%02d:%02d".format(hours, minutes, seconds)
    }

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
                        sets = exercise.sets + ExerciseSetUI()
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
                val api = apiService
                if (api == null) {
                    Log.e("WorkoutSessionVM", "API service is null!")
                    return@launch
                }

                Log.d("WorkoutSessionVM", "Deleting session: ${_uiState.value.id}")
                api.deleteWorkoutSession(_uiState.value.id)
                Log.d("WorkoutSessionVM", "Deleted session: ${_uiState.value.id}")

                _sessionDeleted.value = true
                onSuccess()

            } catch (e: Exception) {
                Log.e("WorkoutSessionVM", "Failed to delete session: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun removeSet(exerciseId: String, index: Int, onEmpty: () -> Unit) {
        _uiState.update { state ->
            val updatedSessions = state.exerciseSessions.map { session ->
                if (session.id == exerciseId) {
                    val newSets = session.sets.toMutableList()
                    if (index in newSets.indices) {
                        newSets.removeAt(index)
                    }
                    if (newSets.isEmpty()) onEmpty()
                    session.copy(sets = newSets)
                } else session
            }
            state.copy(exerciseSessions = updatedSessions)
        }
    }

    fun setSessionDeleted() {
        _sessionDeleted.value = true
    }
}
