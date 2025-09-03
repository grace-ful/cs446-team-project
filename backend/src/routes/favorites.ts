import { Router } from "express";
import prisma from "src/lib/prisma";
import { AuthRequest } from "src/lib/types";
import authMiddleware from "src/middleware/authMiddleware";

const favoritesRouter = Router();

// POST /favorites/exercises/:exerciseTemplateID -> mark favorite
// DELETE /favorites/exercises/:exerciseTemplateID -> unmark
// GET /favorites/exercises -> list my favs
// GET /favorites/exercises/:exerciseTemplateID -> check if fav

// for workouts, same as above

favoritesRouter.get('/', (req, res) => {
    res.json({
        msg: "Hi from favorites router"
    });
});

favoritesRouter.post('/exercises/:exerciseTemplateID', authMiddleware, async (req: AuthRequest, res) => {
    try {
      const userId = req.userId!;
      const exerciseTemplateID = req.params.exerciseTemplateID?.trim();
      if (!exerciseTemplateID) {
        res.status(400).json({ error: "Invalid exerciseTemplateID" });
        return;
      }

      // Optional: check template exists for nicer 404s
      const exists = await prisma.exerciseTemplate.findUnique({ where: { id: exerciseTemplateID } });
      if (!exists) {
        res.status(404).json({ error: "ExerciseTemplate not found" });
        return;
      }

      const fav = await prisma.favoriteExercise.upsert({
        where: { userId_exerciseTemplateID: { userId, exerciseTemplateID } },
        create: { userId, exerciseTemplateID },
        update: {},
      });

      res.status(201).json({ ok: true, favorited: true, createdAt: fav.createdAt });
    } catch (e) {
        res.status(500).json({ error: "Failed to add to favorites" });
    }
});

favoritesRouter.delete(
  "/exercises/:exerciseTemplateID",
  authMiddleware,
  async (req: AuthRequest, res) => {
    try {
      const userId = req.userId!;
      const exerciseTemplateID = req.params.exerciseTemplateID?.trim();
      if (!exerciseTemplateID) {
        res.status(400).json({ error: "Invalid exerciseTemplateID" });
        return;
      }

      await prisma.favoriteExercise
        .delete({ where: { userId_exerciseTemplateID: { userId, exerciseTemplateID } } })
        .catch(() => null); // ignore if not found

      res.json({ ok: true, favorited: false });
    } catch (e: any) {
      console.error("unfavoriteExercise error:", e.message);
      res.status(500).json({ error: "Failed to remove from favorites" });
    }
  }
);

favoritesRouter.get(
  "/exercises/:exerciseTemplateID",
  authMiddleware,
  async (req: AuthRequest, res) => {
    try {
      const userId = req.userId!;
      const exerciseTemplateID = req.params.exerciseTemplateID?.trim();
      if (!exerciseTemplateID) res.status(400).json({ error: "Invalid exerciseTemplateID" });

      const fav = await prisma.favoriteExercise.findUnique({
        where: { userId_exerciseTemplateID: { userId, exerciseTemplateID } },
        select: { createdAt: true },
      });

      res.json({ favorited: Boolean(fav), createdAt: fav?.createdAt ?? null });
    } catch (e: any) {
      console.error("isExerciseFavorited error:", e.message);
      res.status(500).json({ error: "Failed to fetch favorite status" });
    }
  }
);

favoritesRouter.get(
  "/exercises",
  authMiddleware,
  async (req: AuthRequest, res) => {
    try {
      const userId = req.userId!;
      const take = Math.min(Number(req.query.take ?? 50), 100);
      const skip = Math.max(Number(req.query.skip ?? 0), 0);

      const [rows, total] = await Promise.all([
        prisma.favoriteExercise.findMany({
          where: { userId },
          include: { exerciseTemplate: true },
          orderBy: { createdAt: "desc" },
          take,
          skip,
        }),
        prisma.favoriteExercise.count({ where: { userId } }),
      ]);

      res.json({
        exercises: rows.map((r) => r.exerciseTemplate),
        total,
        skip,
        take,
      });
    } catch (e: any) {
      console.error("listFavoriteExercises error:", e.message);
      res.status(500).json({ error: "Failed to fetch favorites" });
    }
  }
);

export default favoritesRouter;