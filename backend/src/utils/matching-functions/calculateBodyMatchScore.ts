import { User } from "@prisma/client";
import { scoreAge, scoreHeight, scoreWeight } from "./matching-helper-functions";

export function calculateBodyMatchScore(userA: User, userB: User): number {
  return (
    scoreHeight(userA, userB, 25, 15) +
    scoreWeight(userA, userB, 25, 15) +
    scoreAge(userA, userB, 10)
  );
}
