import { PrismaClient, BodyPart } from "@prisma/client";
import prisma from "../lib/prisma";

async function seedWorkoutTemplates() {
  const bodyParts: BodyPart[] = ["ABS", "ARMS", "CARDIO", "CHEST", "LEGS"];

  const DEFAULT_USER_ID = "621b6f5d-aa5d-422b-bd15-87f23724396c";

  // Step 1: Delete all ExerciseSets
    const deletedSets = await prisma.exerciseSet.deleteMany({});
    console.log(`🗑️ Deleted ${deletedSets.count} exercise sets.`);

    // Step 2: Delete all ExerciseSessions
    const deletedExerciseSessions = await prisma.exerciseSession.deleteMany({});
    console.log(`🗑️ Deleted ${deletedExerciseSessions.count} exercise sessions.`);

    // Step 3: Delete all WorkoutSessions
    const deletedWorkoutSessions = await prisma.workoutSession.deleteMany({});
    console.log(`🗑️ Deleted ${deletedWorkoutSessions.count} workout sessions.`);

    // Step 4: Delete all WorkoutTemplates
    const deletedWorkoutTemplates = await prisma.workoutTemplate.deleteMany({});
    console.log(`🗑️ Deleted ${deletedWorkoutTemplates.count} workout templates.`);
  console.log("🧹 Cleared all existing workout templates.");

  const templates = [];

  for (const part of bodyParts) {
    const exercises = await prisma.exerciseTemplate.findMany({
      where: {
        bodyPart: part,
        isGeneral: true,
      },
      take: 4,
    });

    if (exercises.length === 0) {
      console.log(`⚠️ No exercises found for ${part}`);
      continue;
    }

    const template = await prisma.workoutTemplate.create({
      data: {
        name: `General ${part} Workout`,
        isGeneral: true,
        exercises: {
          connect: exercises.map((e) => ({ id: e.id })),
        },
      },
      include: { exercises: true },
    });

    templates.push(template);
  }

  console.log(JSON.stringify(templates, null, 2));
  console.log("✅ Workout templates created.");
}

seedWorkoutTemplates()
  .catch((e) => {
    console.error("❌ Error creating templates:", e);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
