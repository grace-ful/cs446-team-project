
package com.example.cs446_fit4me.model

data class Exercise(
    val name: String,
    val muscleGroup: MuscleGroup,
    val equipment: Equipment,
    val bodyPart: BodyPart,
    val description: String,
    val isGeneric: Boolean,
    val imageUrl: String? = null
)

enum class MuscleGroup { SHOULDERS, BICEPS, HAMSTRINGS, CALVES, GLUTES, LATS, CHEST, QUADS, ABS, TRICEPS, OTHER }
enum class Equipment { BARBELLS, BENCH, DUMBBELL, GYM_MAT, INCLINE_BENCH, KETTLEBELL, PULL_UP_BAR, SZ_BAR, SWISS_BALL, NONE, OTHER }
enum class BodyPart { ABS, ARMS, BACK, CARDIO, CALVES, CHEST, LEGS, SHOULDERS, OTHER }


data class ExerciseTemplate(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val bodyPart: String,
    val equipment: String,
    val isGeneral: Boolean,
    val imageURL: String?,
    val userId: String?,
    val createdAt: String
)

fun String.toMuscleGroupOrNull() = try { MuscleGroup.valueOf(this.uppercase()) } catch (e: Exception) { null }
fun String.toEquipmentOrNull() = try { Equipment.valueOf(this.uppercase()) } catch (e: Exception) { null }
fun String.toBodyPartOrNull() = try { BodyPart.valueOf(this.uppercase()) } catch (e: Exception) { null }


// if we want to convert, just keeping for now we can remove later
fun ExerciseTemplate.toExercise(): Exercise {
    return Exercise(
        name = name,
        muscleGroup = muscleGroup.toMuscleGroupOrNull() ?: MuscleGroup.OTHER, // fallback
        equipment = equipment.toEquipmentOrNull() ?: Equipment.NONE,
        bodyPart = bodyPart.toBodyPartOrNull() ?: BodyPart.OTHER,
        description = "",
        isGeneric = isGeneral,
        imageUrl = imageURL
    )
}


data class CreateExerciseRequest(
    val name: String,
    val muscleGroup: MuscleGroup,
    val bodyPart: BodyPart,
    val isGeneral: Boolean = false,
    val imageURL: String? = null,
    val userId: String? = null,
    val equipment: Equipment
)