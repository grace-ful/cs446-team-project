import { Router, Request, Response } from "express";
import prisma from "../lib/prisma";

const exerciseSetRouter = Router();

// Create a new ExerciseSet
exerciseSetRouter.post("/", async (req: Request, res: Response): Promise<any> => {
	const { reps, weight, duration, exerciseSessionId } = req.body;

	if (!reps || !exerciseSessionId) {
		return res
			.status(400)
			.json({ error: "Reps and exerciseSessionId are required." });
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
});

// Get ExerciseSets by exerciseSessionId
exerciseSetRouter.get(
	"/by-session/:sessionId",
	async (req: Request, res: Response) => {
		const { sessionId } = req.params;

		try {
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
exerciseSetRouter.put("/:id", async (req: Request, res: Response) => {
	const { id } = req.params;
	const { reps, weight, duration } = req.body;

	try {
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
});

// Delete an ExerciseSet
exerciseSetRouter.delete("/:id", async (req: Request, res: Response) => {
	const { id } = req.params;

	try {
		await prisma.exerciseSet.delete({ where: { id } });
		res.status(204).send();
	} catch (err: any) {
		res.status(400).json({ error: "Failed to delete ExerciseSet." });
	}
});

export default exerciseSetRouter;
