enum class TimePreference { MORNING, AFTERNOON, EVENING, NIGHT, NONE }
enum class ExperienceLevel { BEGINNER, INTERMEDIATE, ADVANCED, ATHLETE, COACH }
enum class GymFrequency { NEVER, RARELY, OCCASIONALLY, REGULARLY, FREQUENTLY, DAILY }

data class MatchEntry(
    val userId: String,
    val matchedUserId: String,
    val score: Float,
    val matchee: MatcheeUser? // You'll need to define this model as well
)

data class MatcheeUser(
    val id: String,
    val name: String,
    val age: Int,
    val heightCm: Float,
    val weightKg: Float,
    val location: String,
    val timePreference: TimePreference,
    val experienceLevel: ExperienceLevel,
    val gymFrequency: GymFrequency,
    // Add any other fields you're including in the response
)
