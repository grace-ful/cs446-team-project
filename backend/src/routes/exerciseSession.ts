import { Router, Request, Response } from "express";
import prisma from "../lib/prisma";
import authMiddleware from "../middleware/authMiddleware";
import { AuthRequest } from "../lib/types";

const exerciseSessionRouter = Router();

// Create ExerciseSession
exerciseSessionRouter.post(
	"/",
	authMiddleware,
	async (req: AuthRequest, res: Response): Promise<any> => {
		const {
			exerciseTemplateID,
			date,
			notes,
			workoutSessionId, // optional
		} = req.body;

		const userId = req.userId;

		if (!exerciseTemplateID || !userId || !date) {
			return res.status(400).json({ error: "Missing required fields." });
		}

		try {
			const session = await prisma.exerciseSession.create({
				data: {
					exerciseTemplateID,
					userId,
					date: new Date(date),
					notes,
					workoutSessionId,
				},
			});

			res.status(201).json(session);
		} catch (err: any) {
			res.status(400).json({ error: err.message });
		}
	}
);

// Get Exercise History for User
exerciseSessionRouter.get(
  "/by-user",
  authMiddleware,
  async (req: AuthRequest, res: Response): Promise<any> => {
    const userId = req.userId;

    try {
      const history = await prisma.exerciseSession.findMany({
        where: { userId },
        include: {
          exerciseTemplate: true,
          sets: true,
        },
        orderBy: {
          date: "desc",
        },
      });

      // Group by exerciseTemplateId
      const grouped = history.reduce((acc, session) => {
        const key = session.exerciseTemplateID;
        if (!acc[key]) {
          acc[key] = {
            exerciseId: key,
            exerciseName: session.exerciseTemplate?.name ?? "Unknown",
            sessions: [],
          };
        }

        acc[key].sessions.push({
          sessionId: session.id,
          date: session.date,
          sets: session.sets,
        });

        return acc;
      }, {} as Record<string, any>);

      res.status(200).json(Object.values(grouped));
    } catch (err: any) {
      res.status(500).json({ 
		msg: "Hey",
		error: err.message });
    }
  }
);

// Get by ID
exerciseSessionRouter.get(
	"/:id",
	authMiddleware,
	async (req: AuthRequest, res: Response): Promise<any> => {
		const id = req.params.id;
		const userId = req.userId;

		try {
			const session = await prisma.exerciseSession.findUnique({
				where: { id, userId },
				include: {
					exerciseTemplate: true,
					user: true,
					sets: true,
				},
			});

			if (!session) {
				return res.status(404).json({ error: "ExerciseSession not found." });
			}

			res.status(200).json(session);
		} catch (err: any) {
			res.status(500).json({ error: err.message });
		}
	}
);


export default exerciseSessionRouter;
