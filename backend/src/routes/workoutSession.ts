import { Router, Request, Response } from "express";
import prisma from "../lib/prisma";

const workoutSessionRouter = Router();

// Create a WorkoutSession
workoutSessionRouter.post("/", async (req: Request, res: Response): Promise<any> => {
	const { userId, workoutTemplateId, workoutDate, notes } = req.body;

	if (!userId || !workoutTemplateId) {
		return res
			.status(400)
			.json({ error: "userId and workoutTemplateId are required." });
	}

	try {
		const session = await prisma.workoutSession.create({
			data: {
				userId,
				workoutTemplateId,
				workoutDate: workoutDate ? new Date(workoutDate) : undefined,
				notes,
			},
			include: {
				Workout: true,
				exerciseSessions: true,
			},
		});

		res.status(201).json(session);
	} catch (err: any) {
		res.status(400).json({ error: err.message });
	}
});

// Get session by ID
workoutSessionRouter.get(
	"/:id",
	async (req: Request, res: Response): Promise<any> => {
		try {
			const session = await prisma.workoutSession.findUnique({
				where: { id: req.params.id },
				include: {
					Workout: true,
					User: true,
					exerciseSessions: {
						include: {
							exerciseTemplate: true,
							sets: true,
						},
					},
				},
			});

			if (!session) {
				return res.status(404).json({ error: "WorkoutSession not found." });
			}

			res.status(200).json(session);
		} catch (err: any) {
			res.status(500).json({ error: err.message });
		}
	}
);

// Get all sessions for a user
workoutSessionRouter.get("/by-user/:userId", async (req: Request, res: Response) => {
	try {
		const sessions = await prisma.workoutSession.findMany({
			where: { userId: req.params.userId },
			include: {
				Workout: true,
				exerciseSessions: true,
			},
			orderBy: {
				workoutDate: "desc",
			},
		});

		res.status(200).json(sessions);
	} catch (err: any) {
		res.status(500).json({ error: err.message });
	}
});

// Update session (notes/date)
workoutSessionRouter.put("/:id", async (req: Request, res: Response) => {
	const { notes, workoutDate } = req.body;

	try {
		const updated = await prisma.workoutSession.update({
			where: { id: req.params.id },
			data: {
				notes,
				workoutDate: workoutDate ? new Date(workoutDate) : undefined,
			},
		});

		res.status(200).json(updated);
	} catch (err: any) {
		res.status(400).json({ error: "Failed to update WorkoutSession." });
	}
});

// Delete session
workoutSessionRouter.delete("/:id", async (req: Request, res: Response) => {
	try {
		await prisma.workoutSession.delete({ where: { id: req.params.id } });
		res.status(204).send();
	} catch (err: any) {
		res.status(400).json({ error: "Failed to delete WorkoutSession." });
	}
});

export default workoutSessionRouter;
