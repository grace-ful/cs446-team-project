package com.example.cs446_fit4me.network

import com.example.cs446_fit4me.model.LoginRequest
import com.example.cs446_fit4me.model.SignupRequest
import com.example.cs446_fit4me.model.UpdateUserRequest
import com.example.cs446_fit4me.model.UserResponse
import com.example.cs446_fit4me.model.UpdateUserRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.PUT

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

}
