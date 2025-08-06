package com.example.cs446_fit4me.network

import com.example.cs446_fit4me.model.LoginRequest
import com.example.cs446_fit4me.model.SignupRequest
import com.example.cs446_fit4me.model.UpdateUserRequest
import com.example.cs446_fit4me.model.UserResponse
import com.example.cs446_fit4me.model.UpdateMatchStrategyRequest
import com.example.cs446_fit4me.model.UpdatePrivacyRequest
import com.example.cs446_fit4me.model.UpdateGenderMatchingPreferenceRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path

interface UserApiService {

    @POST("user/signup")
    suspend fun signup(@Body request: SignupRequest): UserResponse

    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): UserResponse

    @GET("user/{id}")
    suspend fun getUserById(): UserResponse

    @PUT("user/{id}")
    suspend fun updateUser(
        @Path("id") userId: String,
        @Body updateData: UpdateUserRequest
    ): UserResponse

    @GET("user/by-id/{id}")
    suspend fun getUserByIdByPath(@Path("id") userId: String): UserResponse

    @PUT("user/update-match-strategy")
    suspend fun updateMatchStrategy(
        @Body body: UpdateMatchStrategyRequest
    ): UserResponse

    @PUT("user/update-privacy")
    suspend fun updatePrivacy(
        @Body body: UpdatePrivacyRequest
    ): UserResponse

    @PUT("user/update-gender-matching-preference")
    suspend fun updateGenderMatchingPreference(
        @Body body: UpdateGenderMatchingPreferenceRequest
    ): UserResponse

    @DELETE("user/{id}")
    suspend fun deleteUser(
        @Path("id") userId: String
    ): retrofit2.Response<Void>

}
