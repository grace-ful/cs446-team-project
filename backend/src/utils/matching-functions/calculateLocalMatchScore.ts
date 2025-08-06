import { User } from "@prisma/client";
import { scoreGymFrequency, scoreLocation, scoreTimePreference } from "./matching-helper-functions";

export function calculateLocalMatchScore(userA: User, userB: User): number {
  return (
    scoreLocation(userA, userB, 40) +
    scoreTimePreference(userA, userB, 30) +
    scoreGymFrequency(userA, userB, 20, 10)
  );
}
