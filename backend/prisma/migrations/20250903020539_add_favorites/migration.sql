-- CreateTable
CREATE TABLE "FavoriteExercise" (
    "userId" TEXT NOT NULL,
    "exerciseTemplateID" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "FavoriteExercise_pkey" PRIMARY KEY ("userId","exerciseTemplateID")
);

-- CreateTable
CREATE TABLE "FavoriteWorkout" (
    "userId" TEXT NOT NULL,
    "workoutTemplateID" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "FavoriteWorkout_pkey" PRIMARY KEY ("userId","workoutTemplateID")
);

-- CreateIndex
CREATE INDEX "FavoriteExercise_userId_createdAt_idx" ON "FavoriteExercise"("userId", "createdAt");

-- CreateIndex
CREATE INDEX "FavoriteExercise_exerciseTemplateID_idx" ON "FavoriteExercise"("exerciseTemplateID");

-- CreateIndex
CREATE INDEX "FavoriteWorkout_userId_createdAt_idx" ON "FavoriteWorkout"("userId", "createdAt");

-- CreateIndex
CREATE INDEX "FavoriteWorkout_workoutTemplateID_idx" ON "FavoriteWorkout"("workoutTemplateID");

-- AddForeignKey
ALTER TABLE "FavoriteExercise" ADD CONSTRAINT "FavoriteExercise_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FavoriteExercise" ADD CONSTRAINT "FavoriteExercise_exerciseTemplateID_fkey" FOREIGN KEY ("exerciseTemplateID") REFERENCES "ExerciseTemplate"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FavoriteWorkout" ADD CONSTRAINT "FavoriteWorkout_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FavoriteWorkout" ADD CONSTRAINT "FavoriteWorkout_workoutTemplateID_fkey" FOREIGN KEY ("workoutTemplateID") REFERENCES "WorkoutTemplate"("id") ON DELETE CASCADE ON UPDATE CASCADE;
