import { User } from "@prisma/client";
import { scoreGymFrequency, scoreLocation, scoreTimePreference } from "./matching-helper-functions";

export function calculateBeginnerMatchScore(userA: User, userB: User): number {
  let score = 0;

  const beginnerTiers = ["BEGINNER", "INTERMEDIATE"];
  if (beginnerTiers.includes(userB.experienceLevel)) {
    score += 40;
  }

  return (
    score +
    scoreGymFrequency(userA, userB, 20, 10) +
    scoreLocation(userA, userB, 20) +
    scoreTimePreference(userA, userB, 15)
  );
}
