package com.example.cs446_fit4me.network

import MatchEntry
import retrofit2.Response
import retrofit2.http.*

interface MatchingApiService {

    // GET /matches/by-user/{userId} – Get top matches for a user
    @GET("matches/by-user/{userId}")
    suspend fun getUserMatches(): List<MatchEntry>

    @POST("matches/update/{userId}")
    suspend fun updateMatches(): Response<Void>
}
