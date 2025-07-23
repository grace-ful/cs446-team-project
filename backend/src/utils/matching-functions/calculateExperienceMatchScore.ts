import { User } from "@prisma/client";
import { scoreAge, scoreExperienceLevel, scoreGymFrequency, scoreWeight } from "./matching-helper-functions";

export function calculateExperienceMatchScore(userA: User, userB: User): number {
  return (
    scoreExperienceLevel(userA, userB, 40, 20) +
    scoreGymFrequency(userA, userB, 30, 15) +
    scoreAge(userA, userB, 10) +
    scoreWeight(userA, userB, 10, 5)
  );
}
