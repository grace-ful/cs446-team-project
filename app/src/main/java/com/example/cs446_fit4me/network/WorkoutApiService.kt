package com.example.cs446_fit4me.network

import com.example.cs446_fit4me.model.*
import retrofit2.http.*
import retrofit2.Response


interface WorkoutApiService {

    @GET("workout-template/general")
    suspend fun getGeneralWorkouts(): List<WorkoutTemplateResponse>

    @POST("workout-template/add")
    suspend fun createWorkoutTemplate(
        @Body request: CreateWorkoutTemplateRequest
    ): WorkoutTemplateResponse

    @GET("workout-template/by-user/{userId}")
    suspend fun getUserWorkouts(
        @Path("userId") userId: String
    ): List<WorkoutTemplateResponse>

    @PUT("workout-template/{id}/add-exercises")
    suspend fun addExercisesToTemplate(
        @Path("id") id: String,
        @Body request: ExerciseIdListRequest
    ): WorkoutTemplateResponse

    @HTTP(method = "DELETE", path = "workout-template/{id}/remove-exercise", hasBody = true)
    suspend fun removeExerciseFromTemplate(
        @Path("id") id: String,
        @Body request: RemoveExerciseRequest
    ): WorkoutTemplateResponse

    @PUT("workout-template/{id}/update-name")
    suspend fun updateWorkoutTemplateName(
        @Path("id") id: String,
        @Body request: UpdateWorkoutNameRequest
    ): WorkoutTemplateResponse

    @DELETE("workout-template/{id}")
    suspend fun deleteWorkoutTemplate(@Path("id") id: String): Response<Unit>


}