-- CreateEnum
CREATE TYPE "matchStrategy" AS ENUM ('BALANCED', 'SCHEDULE', 'EXPERIENCE', 'LOCAL', 'BODY', 'BEGINNER');

-- AlterTable
ALTER TABLE "User" ADD COLUMN     "matchStrategy" "matchStrategy" NOT NULL DEFAULT 'BALANCED';
