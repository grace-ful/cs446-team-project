package com.example.cs446_fit4me.network

import com.example.cs446_fit4me.model.ExerciseTemplate
import retrofit2.http.*
import com.example.cs446_fit4me.model.CreateExerciseRequest

interface ExerciseApiService {

    // GET /exercise-template/general
    @GET("exercise-template/general")
    suspend fun getGeneralExercises(): List<ExerciseTemplate>

    // GET /exercise-template/by-user/{userId}
    @GET("exercise-template/by-user/{userId}")
    suspend fun getUserExercises(@Path("userId") userId: String): List<ExerciseTemplate>

    // GET /exercise-template/{id}
    @GET("exercise-template/{id}")
    suspend fun getExerciseById(@Path("id") id: String): ExerciseTemplate

    // POST /exercise-template
    @POST("exercise-template/")
    suspend fun createExercise(@Body newExercise: CreateExerciseRequest): ExerciseTemplate

    // PUT /exercise-template/{id}
    @PUT("exercise-template/{id}")
    suspend fun updateExercise(
        @Path("id") id: String,
        @Body updatedExercise: ExerciseTemplate
    ): ExerciseTemplate

    // DELETE /exercise-template/{id}
    @DELETE("exercise-template/{id}")
    suspend fun deleteExercise(@Path("id") id: String): retrofit2.Response<Unit>
}
