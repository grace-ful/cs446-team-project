import { User } from "@prisma/client";
import { scoreGymFrequency, scoreLocation, scoreTimePreference } from "./matching-helper-functions";

export function calculateScheduleMatchScore(userA: User, userB: User): number {
  return (
    scoreTimePreference(userA, userB, 30) +
    scoreGymFrequency(userA, userB, 20, 10) +
    scoreLocation(userA, userB, 30)
  );
}