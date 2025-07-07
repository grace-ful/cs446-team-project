package com.example.cs446_fit4me.network

import com.example.cs446_fit4me.model.CreateWorkoutTemplateRequest
import com.example.cs446_fit4me.model.WorkoutTemplateResponse
import retrofit2.http.*

interface WorkoutApiService {

    // GET /workout-template/
    @GET("workout-template/")
    suspend fun getWorkoutTemplateRoot(): Map<String, String>

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
