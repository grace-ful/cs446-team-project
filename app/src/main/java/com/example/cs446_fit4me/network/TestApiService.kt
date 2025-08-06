package com.example.cs446_fit4me.network

import retrofit2.Call
import retrofit2.http.GET

interface TestApiService {
    @GET("test")
    fun checkStatus(): Call<TestResponse>
}

data class TestResponse(
    val status: String
)
