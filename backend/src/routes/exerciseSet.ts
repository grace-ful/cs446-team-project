import { Router, Request, Response } from "express";
import prisma from "../lib/prisma";
import authMiddleware from "src/middleware/authMiddleware";
import { AuthRequest } from "src/lib/types";

const exerciseSetRouter = Router();

// Create a new ExerciseSet
exerciseSetRouter.post(
  "/",
  authMiddleware,
  async (req: AuthRequest, res: Response): Promise<any> => {
    const { reps, weight, duration, exerciseSessionId } = req.body;

    if (!reps || !exerciseSessionId) {
      return res
        .status(400)
        .json({ error: "Reps and exerciseSessionId are required." });
    }

    const session = await prisma.exerciseSession.findUnique({
      where: { id: exerciseSessionId },
      select: { userId: true },
    });

    if (!session || session.userId !== req.userId) {
      return res
        .status(403)
        .json({ error: "Not authorized to add a set to this session." });
    }

    try {
      const newSet = await prisma.exerciseSet.create({
        data: {
          reps,
          weight,
          duration,
          exerciseSessionId,
        },
      });

      res.status(201).json(newSet);
    } catch (err: any) {
      res.status(400).json({ error: err.message });
    }
  }
);

// Get ExerciseSets by exerciseSessionId
exerciseSetRouter.get(
  "/by-session/:sessionId",
  authMiddleware,
  async (req: AuthRequest, res: Response) => {
    const { sessionId } = req.params;

    try {
      const session = await prisma.exerciseSession.findUnique({
        where: { id: sessionId },
        select: { userId: true },
      });

      if (!session || session.userId !== req.userId) {
        res
          .status(403)
          .json({ error: "Not authorized to view sets for this session." });
        return;
      }

      const sets = await prisma.exerciseSet.findMany({
        where: { exerciseSessionId: sessionId },
        orderBy: { createdAt: "asc" },
      });

      res.status(200).json(sets);
    } catch (err: any) {
      res.status(500).json({ error: err.message });
    }
  }
);

// Update an ExerciseSet
exerciseSetRouter.put(
  "/:id",
  authMiddleware,
  async (req: AuthRequest, res: Response): Promise<any> => {
    const { id } = req.params;
    const { reps, weight, duration } = req.body;

    try {
      const set = await prisma.exerciseSet.findUnique({
        where: { id },
        include: { ExerciseSession: { select: { userId: true } } },
      });

      if (!set || set.ExerciseSession.userId !== req.userId) {
        return res
          .status(403)
          .json({ error: "Not authorized to modify this set." });
      }

      const updatedSet = await prisma.exerciseSet.update({
        where: { id },
        data: {
          reps,
          weight,
          duration,
        },
      });

      res.status(200).json(updatedSet);
    } catch (err: any) {
      res.status(400).json({ error: "Failed to update ExerciseSet." });
    }
  }
);

// Delete an ExerciseSet
exerciseSetRouter.delete(
  "/:id",
  authMiddleware,
  async (req: AuthRequest, res: Response): Promise<any> => {
    const { id } = req.params;

    try {
      const set = await prisma.exerciseSet.findUnique({
        where: { id },
        include: { ExerciseSession: { select: { userId: true } } },
      });

      if (!set || set.ExerciseSession.userId !== req.userId) {
        return res
          .status(403)
          .json({ error: "Not authorized to modify this set." });
      }


      await prisma.exerciseSet.delete({ where: { id } });
      res.status(204).send();
    } catch (err: any) {
      res.status(400).json({ error: "Failed to delete ExerciseSet." });
    }
  }
);

export default exerciseSetRouter;
