import { Router, Request, Response } from "express";
import prisma from "../lib/prisma";

const exerciseSessionRouter = Router();

// Create ExerciseSession
exerciseSessionRouter.post(
	"/",
	async (req: Request, res: Response): Promise<any> => {
		const {
			exerciseTemplateID,
			userId,
			date,
			notes,
			workoutSessionId, // optional
		} = req.body;

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

// Get by ID
exerciseSessionRouter.get(
	"/:id",
	async (req: Request, res: Response): Promise<any> => {
		const id = req.params.id;

		try {
			const session = await prisma.exerciseSession.findUnique({
				where: { id },
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
