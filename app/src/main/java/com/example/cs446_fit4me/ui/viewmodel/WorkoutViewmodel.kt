package com.example.cs446_fit4me.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.network.ApiClient
import kotlinx.coroutines.launch
import java.util.*

class WorkoutViewModel : ViewModel() {

    val allExercises = mutableStateListOf<ExerciseTemplate>()
    val isLoadingExercises = mutableStateOf(false)

    private var hasFetchedExercises = false

    fun fetchAllExerciseTemplates(context: Context) {
        if (hasFetchedExercises) return

        viewModelScope.launch {
            isLoadingExercises.value = true
            try {
                val general = ApiClient.getExerciseApi(context).getGeneralExercises()
                val user = ApiClient.getExerciseApi(context).getUserExercises()
                allExercises.clear()
                allExercises.addAll(general + user)
                hasFetchedExercises = true
                Log.d("WorkoutViewModel", "Fetched ${allExercises.size} exercises.")
            } catch (e: Exception) {
                Log.e("WorkoutViewModel", "Error fetching exercises", e)
            } finally {
                isLoadingExercises.value = false
            }
        }
    }

    fun createWorkoutTemplateOnServer(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val api = ApiClient.getWorkoutApi(context)

                val response = api.createWorkoutTemplate(
                    CreateWorkoutTemplateRequest(
                        name = workoutName,
                        exerciseIds = selectedExercises.map { it.id }
                    )
                )
                Log.d("WorkoutViewModel", "Created workout: $response")
                clearSelectedExercises()
                updateWorkoutName("")
                onSuccess()

            } catch (e: Exception) {
                onError("Error: ${e.localizedMessage}")
            }
        }
    }

    var workoutName by mutableStateOf("")
        private set

    fun updateWorkoutName(newName: String) {
        workoutName = newName
    }

    private val _myWorkouts = mutableStateListOf<WorkoutModel>()
    val myWorkouts: List<WorkoutModel> get() = _myWorkouts

    private val _standardWorkouts = mutableStateListOf<WorkoutModel>()
    val standardWorkouts: List<WorkoutModel> get() = _standardWorkouts

    val selectedExercises = mutableStateListOf<ExerciseTemplate>()

    fun fetchStandardWorkouts(context: Context) {
        viewModelScope.launch {
            try {
                val response = ApiClient.getWorkoutApi(context).getGeneralWorkouts()
                _standardWorkouts.clear()
                _standardWorkouts.addAll(response.map { it.toWorkoutModel() })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun fetchUserWorkouts(context: Context, userId: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.getWorkoutApi(context).getUserWorkouts(userId)
                Log.d("WorkoutViewModel", "Received ${response.size} user workouts from backend.")
                _myWorkouts.clear()
                _myWorkouts.addAll(response.map { it.toWorkoutModel() })
            } catch (e: Exception) {
                println("Error loading user workouts: ${e.message}")
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

    fun addExercise(exercise: ExerciseTemplate) {
        if (selectedExercises.none { it.id == exercise.id }) {
            selectedExercises.add(exercise)
        }
    }

    fun removeExercise(id: String) {
        selectedExercises.removeIf { it.id == id }
    }

    fun clearSelectedExercises() {
        selectedExercises.clear()
    }

    // Update workout function
    fun updateWorkout(updatedWorkout: WorkoutModel) {
        val index = _myWorkouts.indexOfFirst { it.id == updatedWorkout.id }
        if (index != -1) {
            _myWorkouts[index] = updatedWorkout
        }
        // (optional) update standard workouts as well
        /*
        val stdIndex = _standardWorkouts.indexOfFirst { it.id == updatedWorkout.id }
        if (stdIndex != -1) {
            _standardWorkouts[stdIndex] = updatedWorkout
        }
        */
    }

    fun startWorkoutSessionFromTemplate(
        context: Context,
        templateId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                print("Starting workout session from template: $templateId");
                val response = ApiClient.getWorkoutSessionApi(context)
                    .startWorkoutSessionFromTemplate(templateId)
                onSuccess(response.workoutSessionId)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
}
