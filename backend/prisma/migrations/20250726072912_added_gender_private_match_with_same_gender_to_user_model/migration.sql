-- CreateEnum
CREATE TYPE "Gender" AS ENUM ('MALE', 'FEMALE', 'NON_BINARY', 'OTHER');

-- AlterTable
ALTER TABLE "User" ADD COLUMN     "gender" "Gender" NOT NULL DEFAULT 'MALE',
ADD COLUMN     "matchWithSameGender" BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN     "private" BOOLEAN NOT NULL DEFAULT false;
