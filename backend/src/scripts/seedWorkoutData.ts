import { PrismaClient, BodyPart } from "@prisma/client";
import prisma from "../lib/prisma";

async function seedWorkoutTemplates() {
  const bodyParts: BodyPart[] = ["ABS", "ARMS", "CARDIO", "CHEST", "LEGS"];
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

  console.log("✅ Workout templates created.");
  console.log(JSON.stringify(templates, null, 2));
}

seedWorkoutTemplates()
  .catch((e) => {
    console.error("❌ Error creating templates:", e);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });