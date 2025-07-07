import prisma from "../lib/prisma";
import fs from "fs";
import path from "path";
import { MuscleGroup, BodyPart, Equipment } from "@prisma/client";

const validMuscleGroups = Object.values(MuscleGroup);
const validBodyParts = Object.values(BodyPart);
const validEquipment = Object.values(Equipment);

function toValidEnum<T extends string>(
  value: string | null | undefined,
  validValues: T[],
  fallback: T
): T {
  if (!value) return fallback;
  const formatted = value.replace(/\s+/g, "_").toUpperCase();
  return validValues.includes(formatted as T) ? (formatted as T) : fallback;
}

async function main() {
  const filePath = path.resolve(__dirname, "../../dist/scripts/output.json");
  const data = JSON.parse(fs.readFileSync(filePath, "utf-8"));

  await prisma.pR.deleteMany({
    where: {
      template: {
        isGeneral: true,
      },
    },
  });

  await prisma.exerciseSession.deleteMany({
  where: {
    exerciseTemplate: {
      isGeneral: true,
    },
  },
});

  // Step 1: Clear existing general templates
  await prisma.exerciseTemplate.deleteMany({
    where: { isGeneral: true },
  });
  console.log("🧹 Cleared existing general ExerciseTemplates");

  for (const item of data) {
    const muscleGroup = toValidEnum(
      item.muscleGroup,
      validMuscleGroups,
      MuscleGroup.OTHER
    );
    const bodyPart = toValidEnum(item.bodyPart, validBodyParts, BodyPart.OTHER);

    const equipmentRaw = item.equipment ?? "";
    const equipment =
      equipmentRaw.toLowerCase().includes("bodyweight") ||
      equipmentRaw.toLowerCase().includes("none")
        ? Equipment.NONE
        : toValidEnum(equipmentRaw, validEquipment, Equipment.OTHER);

    try {
      await prisma.exerciseTemplate.create({
        data: {
          id: item.id.toString(),
          name: item.name ?? "Unnamed Exercise",
          muscleGroup,
          bodyPart,
          equipment,
          imageURL: item.imageURL ?? null,
          isGeneral: item.isGeneric ?? true,
          userId: null,
        },
      });
    } catch (error: any) {
      console.error(
        `❌ Failed to insert exercise ID ${item.id}:`,
        error.message
      );
    }
  }

  console.log("✅ Finished seeding ExerciseTemplates with equipment 🏋️");
}

main()
  .catch((e) => console.error("❌ Script crashed:", e))
  .finally(() => prisma.$disconnect());
