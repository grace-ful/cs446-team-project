import { User } from "@prisma/client";
import { MatchStrategy } from "src/lib/types";
import { calculateBalancedMatchScore } from "./calculateBalancedMatchScore";
import { calculateScheduleMatchScore } from "./calculateScheduleMatchScore";
import { calculateExperienceMatchScore } from "./calculateExperienceMatchScore";
import { calculateLocalMatchScore } from "./calculateLocalMatchScore";
import { calculateBodyMatchScore } from "./calculateBodyMatchScore";
import { calculateBeginnerMatchScore } from "./calculateBeginnerMatchScore";

export function calculateMatchScore(
  userA: User,
  userB: User,
  strategy: MatchStrategy
): number {
  switch (strategy) {
    case "balanced":
      return calculateBalancedMatchScore(userA, userB);
    case "schedule":
      return calculateScheduleMatchScore(userA, userB);
    case "experience":
      return calculateExperienceMatchScore(userA, userB);
    case "local":
      return calculateLocalMatchScore(userA, userB);
    case "body":
      return calculateBodyMatchScore(userA, userB);
    case "beginner":
      return calculateBeginnerMatchScore(userA, userB);
    default:
      throw new Error(`Unsupported match strategy: ${strategy}`);
  }
}