package com.example.cs446_fit4me.model

// === Enums ===
enum class TimePreference { MORNING, AFTERNOON, EVENING, NIGHT, NONE }
enum class ExperienceLevel { BEGINNER, INTERMEDIATE, ADVANCED, ATHLETE, COACH }
enum class GymFrequency { NEVER, RARELY, OCCASIONALLY, REGULARLY, FREQUENTLY, DAILY }

fun String.toTimePreferenceOrNull() = try { TimePreference.valueOf(this.uppercase()) } catch (e: Exception) { null }
fun String.toExperienceLevelOrNull() = try { ExperienceLevel.valueOf(this.uppercase()) } catch (e: Exception) { null }
fun String.toGymFrequencyOrNull() = try { GymFrequency.valueOf(this.uppercase()) } catch (e: Exception) { null }


// === Requests and Responses ===
data class SignupRequest(
    val email: String,
    val name: String,
    val password: String,
    val heightCm: Int,
    val weightKg: Float,
    val age: Int,
    val location: String,
    val timePreference: TimePreference,
    val experienceLevel: ExperienceLevel,
    val gymFrequency: GymFrequency
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
    val timePreference: TimePreference,
    val experienceLevel: ExperienceLevel,
    val gymFrequency: GymFrequency,
    val createdAt: String,
    val token: String,
)

data class ErrorResponse(val error: String)

data class UpdateUserRequest(
    val name: String? = null,
    val age: Int? = null,
    val heightCm: Int? = null,
    val weightKg: Float? = null,
    val location: String? = null,
    val timePreference: TimePreference? = null,
    val experienceLevel: ExperienceLevel? = null,
    val gymFrequency: GymFrequency? = null,
    val password: String? = null // optional, only if you want to update password
)

data class UserMatch(
    val name: String,
    val age: Int,
    val location: String,
    val timePreference: TimePreference,
    val experienceLevel: ExperienceLevel,
    val gymFrequency: GymFrequency,
    val score: Double
)
