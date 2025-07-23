import { User } from "@prisma/client";

const experienceLevels = ["BEGINNER", "INTERMEDIATE", "ADVANCED", "ATHLETE", "COACH"];

const frequencies = ["NEVER", "RARELY", "OCCASIONALLY", "REGULARLY", "FREQUENTLY", "DAILY"];

// helper functions

export function scoreTimePreference(a: User, b: User, weight: number) {
  return a.timePreference === b.timePreference ? weight : 0;
}

export function scoreExperienceLevel(a: User, b: User, exactWeight: number, nearWeight: number) {
  const diff = Math.abs(experienceLevels.indexOf(a.experienceLevel) - experienceLevels.indexOf(b.experienceLevel));
  return diff === 0 ? exactWeight : diff === 1 ? nearWeight : 0;
}

export function scoreGymFrequency(a: User, b: User, exactWeight: number, nearWeight: number) {
  const diff = Math.abs(frequencies.indexOf(a.gymFrequency) - frequencies.indexOf(b.gymFrequency));
  return diff === 0 ? exactWeight : diff === 1 ? nearWeight : 0;
}

export function scoreAge(a: User, b: User, maxWeight: number) {
  const diff = Math.abs(a.age - b.age);
  return Math.max(0, maxWeight - diff); // e.g., age diff 3 -> score = 7
}

export function scoreHeight(a: User, b: User, closeWeight: number, nearWeight: number) {
  const diff = Math.abs(a.heightCm - b.heightCm);
  return diff <= 2 ? closeWeight : diff <= 5 ? nearWeight : 0;
}

export function scoreWeight(a: User, b: User, closeWeight: number, nearWeight: number) {
  const diff = Math.abs(a.weightKg - b.weightKg);
  return diff <= 5 ? closeWeight : diff <= 10 ? nearWeight : 0;
}

export function scoreLocation(a: User, b: User, weight: number) {
  return a.location === b.location ? weight : 0;
}


// export function calculateMatchScore(userA: User, userB: User): number {
//   let score = 0;

//   // Time Preference
//   if (userA.timePreference === userB.timePreference) score += 10;

//   // Experience Level
//   const experienceLevels = ["BEGINNER", "INTERMEDIATE", "ADVANCED", "ATHLETE", "COACH"];
//   const levelDiff = Math.abs(
//     experienceLevels.indexOf(userA.experienceLevel) - experienceLevels.indexOf(userB.experienceLevel)
//   );
//   if (levelDiff === 0) score += 20;
//   else if (levelDiff === 1) score += 10;

//   // Gym Frequency
//   const frequencies = ["NEVER", "RARELY", "OCCASIONALLY", "REGULARLY", "FREQUENTLY", "DAILY"];
//   const freqDiff = Math.abs(
//     frequencies.indexOf(userA.gymFrequency) - frequencies.indexOf(userB.gymFrequency)
//   );
//   if (freqDiff === 0) score += 15;
//   else if (freqDiff === 1) score += 7;

//   // Age similarity
//   const ageDiff = Math.abs(userA.age - userB.age);
//   score += Math.max(0, 10 - ageDiff); // Max 10, 0 if age diff ≥ 10

//   // Height similarity (in cm)
//   const heightDiff = Math.abs(userA.heightCm - userB.heightCm);
//   if (heightDiff <= 2) score += 10;
//   else if (heightDiff <= 5) score += 5;

//   // Weight similarity
//   const weightDiff = Math.abs(userA.weightKg - userB.weightKg);
//   if (weightDiff <= 5) score += 10;
//   else if (weightDiff <= 10) score += 5;

//   // Location
//   if (userA.location === userB.location) score += 15;

//   return score;
// }
