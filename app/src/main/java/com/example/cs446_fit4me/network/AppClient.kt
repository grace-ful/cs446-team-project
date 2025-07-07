package com.example.cs446_fit4me.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://cs446-team-project-production.up.railway.app/api/" // for Android emulator to access localhost

    private val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val exerciseApiService: ExerciseApiService by lazy {
      instance.create(ExerciseApiService::class.java)
    }

}
