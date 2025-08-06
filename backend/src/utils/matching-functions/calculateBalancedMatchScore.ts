import { User } from "@prisma/client";
import { scoreAge, scoreExperienceLevel, scoreGymFrequency, scoreHeight, scoreLocation, scoreTimePreference, scoreWeight } from "./matching-helper-functions";

export function calculateBalancedMatchScore(userA: User, userB: User): number {
  return (
    scoreTimePreference(userA, userB, 10) +
    scoreExperienceLevel(userA, userB, 20, 10) +
    scoreGymFrequency(userA, userB, 15, 7) +
    scoreAge(userA, userB, 10) +
    scoreHeight(userA, userB, 10, 5) +
    scoreWeight(userA, userB, 10, 5) +
    scoreLocation(userA, userB, 15)
  );
}