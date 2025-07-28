package com.example.cs446_fit4me.model


// RESPONSE MODELS

data class ExerciseSessionHistoryResponse(
    val id: String,
    val exerciseTemplate: ExerciseTemplate?, // reference existing model if available
    val date: String,                        // ISO 8601 string, e.g., "2025-07-26T17:05:00Z"
    val sets: List<ExerciseSetResponse>
)

data class PRResponse(
    val id: String,
    val templateId: String,    // ExerciseTemplate.id
    val userId: String,
    val weight: Float?,
    val duration: Int?,
    val reps: Int,
    val date: String           // ISO date string
)

// UI MODEL FOR EXERCISE HISTORY CARD

data class ExerciseHistoryCardUI(
    val exerciseId: String,
    val exerciseName: String,
    val date: String,                     // Most recent performed date (ISO string)
    val totalSessions: Int,
    val prWeight: Float?,                 // PR (weight), if available
    val prDate: String?,                  // PR set date, if available
    val recentSets: List<ExerciseSetUI> = emptyList()
)
