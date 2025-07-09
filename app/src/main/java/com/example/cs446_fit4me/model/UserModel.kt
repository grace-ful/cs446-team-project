package com.example.cs446_fit4me.model

data class SignupRequest(
    val email: String,
    val name: String,
    val password: String,
    val heightCm: Int,
    val weightKg: Float,
    val age: Int,
    val location: String,
    val timePreference: String,
    val experienceLevel: String,
    val gymFrequency: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val heightCm: Int,
    val weightKg: Float,
    val age: Int,
    val location: String,
    val timePreference: String,
    val experienceLevel: String,
    val gymFrequency: String,
    val createdAt: String
)

data class ErrorResponse(val error: String)
