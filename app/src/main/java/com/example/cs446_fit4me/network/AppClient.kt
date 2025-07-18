package com.example.cs446_fit4me.network

import WorkoutSessionApiService
import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
     const val BASE_URL = "https://cs446-team-project-production.up.railway.app/api/" // PROD
//   const val BASE_URL = "https://aa048e4eb64d.ngrok-free.app/api/" // DEV

    // Create Retrofit instance with AuthInterceptor
    private fun createRetrofit(context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) // Attach JWT
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS) // Increase timeout
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)    // Increase timeout
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Use these in your code: ApiClient.getUserApi(context)
    fun getUserApi(context: Context): UserApiService {
        return createRetrofit(context).create(UserApiService::class.java)
    }

    fun getExerciseApi(context: Context): ExerciseApiService {
        return createRetrofit(context).create(ExerciseApiService::class.java)
    }

    fun getWorkoutApi(context: Context): WorkoutApiService {
        return createRetrofit(context).create(WorkoutApiService::class.java)
    }

    fun getTestApi(context: Context): TestApiService {
        return createRetrofit(context).create(TestApiService::class.java)
    }

    fun getMatchingApi(context: Context): MatchingApiService {
        return createRetrofit(context).create(MatchingApiService::class.java)
    }

    fun getWorkoutSessionApi(context: Context): WorkoutSessionApiService {
        return createRetrofit(context).create(WorkoutSessionApiService::class.java)
    }

    fun getChatApi(context: Context): ChatApiService {
        return createRetrofit(context).create(ChatApiService::class.java)
    }
}
