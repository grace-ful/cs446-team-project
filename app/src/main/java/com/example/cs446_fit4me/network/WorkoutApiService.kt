package com.example.cs446_fit4me.network

import com.example.cs446_fit4me.model.*
import retrofit2.http.*

interface WorkoutApiService {

    @GET("workout-template/general")
    suspend fun getGeneralWorkouts(): List<WorkoutTemplateResponse>


    // POST /workout-template/add
    @POST("workout-template/add")
    suspend fun createWorkoutTemplate(
        @Body request: CreateWorkoutTemplateRequest
    ): WorkoutTemplateResponse

    // PUT /workout-template/{id}/add-exercises
    @PUT("workout-template/{id}/add-exercises")
    suspend fun addExercisesToTemplate(
        @Path("id") id: String,
        @Body request: ExerciseIdListRequest
    ): WorkoutTemplateResponse

    // PUT /workout-template/{id}/remove-exercise
    @PUT("workout-template/{id}/remove-exercise")
    suspend fun removeExerciseFromTemplate(
        @Path("id") id: String,
        @Body request: RemoveExerciseRequest
    ): WorkoutTemplateResponse
}
