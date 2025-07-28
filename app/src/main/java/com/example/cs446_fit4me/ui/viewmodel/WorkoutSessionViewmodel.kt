package com.example.cs446_fit4me.ui.viewmodel

import com.example.cs446_fit4me.network.WorkoutSessionApiService
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.network.ExerciseApiService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed class WorkoutSessionHistoryState {
    object Loading : WorkoutSessionHistoryState()
    data class Success(val sessions: List<WorkoutSessionUI>) : WorkoutSessionHistoryState()
    data class Error(val message: String) : WorkoutSessionHistoryState()
}

sealed class ExerciseHistoryState {
    object Loading : ExerciseHistoryState()
    data class Success(val sessions: List<ExerciseHistoryCardUI>) : ExerciseHistoryState()
    data class Error(val message: String) : ExerciseHistoryState()
}


class WorkoutSessionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutSessionUI())
    val uiState: StateFlow<WorkoutSessionUI> = _uiState

    private val _exerciseHistoryState = MutableStateFlow<ExerciseHistoryState>(ExerciseHistoryState.Loading)
    val exerciseHistoryState: StateFlow<ExerciseHistoryState> = _exerciseHistoryState


    private val _historyState = MutableStateFlow<WorkoutSessionHistoryState>(WorkoutSessionHistoryState.Loading)
    val historyState: StateFlow<WorkoutSessionHistoryState> = _historyState

    private val _sessionDeleted = MutableStateFlow(false)
    val sessionDeleted: StateFlow<Boolean> = _sessionDeleted

    private var apiService: WorkoutSessionApiService? = null
    private var exerciseApiService: ExerciseApiService? = null


    // Timer logic
    private var sessionStartTime: Long? = null
    private var timerJob: Job? = null

    private val _elapsedTime = mutableStateOf("00:00:00")
    val elapsedTime: State<String> get() = _elapsedTime

    fun initApi(context: Context) {
        apiService = ApiClient.getWorkoutSessionApi(context)
        exerciseApiService = ApiClient.getExerciseApi(context)
    }

    // Workout session screen methods

    fun fetchWorkoutSession(sessionId: String) {
        _sessionDeleted.value = false
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

    private fun WorkoutSessionUpdateRequest.debugString(): String =
        buildString {
            appendLine("WorkoutSessionUpdateRequest(")
            exerciseSessions.forEach { ex ->
                appendLine("  ExerciseSessionUpdateRequest(id=${ex.id})")
                ex.sets.forEach { set ->
                    appendLine("    ExerciseSetUpdateRequest(id=${set.id}, reps=${set.reps}, weight=${set.weight}, duration=${set.duration}, isCompleted=${set.isCompleted})")
                }
            }
            append(")")
        }



    fun saveWorkoutSession(durationMillis: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val request = _uiState.value.toFilteredUpdateRequest(durationMillis)

                Log.d(
                    "WorkoutSessionVM",
                    "Saving workout session: sessionId=${_uiState.value.id}, " +
                            "workoutName=${_uiState.value.workoutName}, " +
                            "durationMs=$durationMillis (sec=${durationMillis / 1000})\n" +
                            request.debugString()
                )

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

    // History screen method
    fun fetchWorkoutHistory() {
        Log.d("WorkoutSessionVM", "Fetching history")
        _historyState.value = WorkoutSessionHistoryState.Loading
        viewModelScope.launch {
            try {
                val response = apiService?.getWorkoutSessionsByUser()
                Log.d("WorkoutSessionVM", "Hey!")

                Log.d("WorkoutSessionVM", "Fetched history: $response")
                if (response != null) {
                    val uiSessions = response.map { it.toWorkoutSessionUI() }
                    _historyState.value = WorkoutSessionHistoryState.Success(uiSessions)
                } else {
                    _historyState.value = WorkoutSessionHistoryState.Error("No response from server.")
                }
            } catch (e: Exception) {
                _historyState.value = WorkoutSessionHistoryState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    // Timer methods

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

    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        sessionStartTime = null
        _elapsedTime.value = "00:00:00"
    }

    fun formatElapsedTime(totalMillis: Long): String {
        val totalSeconds = totalMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0)
            "%d:%02d:%02d".format(hours, minutes, seconds)
        else
            "%02d:%02d".format(minutes, seconds)
    }

    // Local state updates for UI

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

    fun resetSessionDeleted() {
        _sessionDeleted.value = false
    }

    fun setSessionDeleted() {
        _sessionDeleted.value = true
    }

    fun fetchExerciseHistory() {
        Log.d("WorkoutSessionVM", "Fetching exercise history")
        _exerciseHistoryState.value = ExerciseHistoryState.Loading

        viewModelScope.launch {
            try {
                val response = exerciseApiService?.getExerciseSessionsByUser()

                if (response != null) {
                    val historyCards = response.map { grouped ->
                        val allSets = grouped.sessions.flatMap { it.sets ?: emptyList() }
                        val prWeight = allSets.maxByOrNull { it.weight ?: 0f }?.weight
                        val mostRecentDate = grouped.sessions.maxByOrNull { it.date }?.date ?: ""

                        val recentSets = grouped.sessions
                            .maxByOrNull { it.date }
                            ?.sets
                            ?.map {
                                ExerciseSetUI(
                                    id = it.id,
                                    reps = it.reps,
                                    weight = it.weight,
                                    duration = it.duration,
                                    isComplete = it.isComplete == true
                                )
                            } ?: emptyList()

                        ExerciseHistoryCardUI(
                            exerciseId = grouped.exerciseId,
                            exerciseName = grouped.exerciseName,
                            date = mostRecentDate,
                            totalSessions = grouped.sessions.size,
                            prWeight = prWeight,
                            prDate = mostRecentDate,
                            recentSets = recentSets
                        )
                    }

                    _exerciseHistoryState.value = ExerciseHistoryState.Success(historyCards)
                } else {
                    _exerciseHistoryState.value = ExerciseHistoryState.Error("No response from server.")
                }
            } catch (e: Exception) {
                Log.e("WorkoutSessionVM", "Error fetching exercise history", e)
                _exerciseHistoryState.value = ExerciseHistoryState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }



}
