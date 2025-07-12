import { User } from "@prisma/client";

export function calculateMatchScore(userA: User, userB: User): number {
  let score = 0;

  // Time Preference
  if (userA.timePreference === userB.timePreference) score += 10;

  // Experience Level
  const experienceLevels = ["BEGINNER", "INTERMEDIATE", "ADVANCED", "ATHLETE", "COACH"];
  const levelDiff = Math.abs(
    experienceLevels.indexOf(userA.experienceLevel) - experienceLevels.indexOf(userB.experienceLevel)
  );
  if (levelDiff === 0) score += 20;
  else if (levelDiff === 1) score += 10;

  // Gym Frequency
  const frequencies = ["NEVER", "RARELY", "OCCASIONALLY", "REGULARLY", "FREQUENTLY", "DAILY"];
  const freqDiff = Math.abs(
    frequencies.indexOf(userA.gymFrequency) - frequencies.indexOf(userB.gymFrequency)
  );
  if (freqDiff === 0) score += 15;
  else if (freqDiff === 1) score += 7;

  // Age similarity
  const ageDiff = Math.abs(userA.age - userB.age);
  score += Math.max(0, 10 - ageDiff); // Max 10, 0 if age diff ≥ 10

  // Height similarity (in cm)
  const heightDiff = Math.abs(userA.heightCm - userB.heightCm);
  if (heightDiff <= 2) score += 10;
  else if (heightDiff <= 5) score += 5;

  // Weight similarity
  const weightDiff = Math.abs(userA.weightKg - userB.weightKg);
  if (weightDiff <= 5) score += 10;
  else if (weightDiff <= 10) score += 5;

  // Location
  if (userA.location === userB.location) score += 15;

  return score;
}
