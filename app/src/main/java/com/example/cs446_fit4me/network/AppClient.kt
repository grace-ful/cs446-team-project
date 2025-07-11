package com.example.cs446_fit4me.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
//    private const val BASE_URL = "https://cs446-team-project-production.up.railway.app/api/" // PROD
    private const val BASE_URL = "http://10.31.176.20:8080/api/" // DEV

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

}
