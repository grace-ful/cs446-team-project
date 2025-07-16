import { Router, Request, Response } from "express";
import prisma from "../lib/prisma";
import authMiddleware from "../middleware/authMiddleware";
import { AuthRequest } from "../lib/types";

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
	const { notes, workoutDate, exerciseSessions } = req.body;

	try {
		const sessionId = req.params.id;

		// Step 1: Update basic workout session fields
		await prisma.workoutSession.update({
			where: { id: sessionId },
			data: {
				notes,
				workoutDate: workoutDate ? new Date(workoutDate) : undefined,
			},
		});

		// Step 2: Loop over each exercise session
		for (const session of exerciseSessions) {
			const exerciseSessionId = session.id;

			for (const set of session.sets) {
				if (set.id) {
					// Update existing set
					await prisma.exerciseSet.update({
						where: { id: set.id },
						data: {
							reps: set.reps,
							weight: set.weight,
							duration: set.duration,
							// Optional: isCompleted if you store it in DB
						},
					});
				} else {
					// Create new set
					await prisma.exerciseSet.create({
						data: {
							reps: set.reps,
							weight: set.weight,
							duration: set.duration,
							ExerciseSession: {
								connect: { id: exerciseSessionId },
							},
						},
					});
				}
			}
		}

		res.status(200).json({ success: true });
	} catch (err: any) {
		console.error(err);
		res.status(400).json({ error: "Failed to update WorkoutSession." });
	}
});


// Delete session
workoutSessionRouter.delete("/:id", async (req: Request, res: Response) => {
	const sessionId = req.params.id;

	try {
		// Find all exercise sessions linked to this workout
		const exerciseSessions = await prisma.exerciseSession.findMany({
			where: { workoutSessionId: sessionId },
			select: { id: true },
		});

		// Delete all sets for each exercise session
		await prisma.exerciseSet.deleteMany({
			where: {
				exerciseSessionId: { in: exerciseSessions.map(es => es.id) }
			}
		});

		// Delete the exercise sessions
		await prisma.exerciseSession.deleteMany({
			where: { workoutSessionId: sessionId }
		});

		// Delete the workout session itself
		await prisma.workoutSession.delete({
			where: { id: sessionId }
		});

		res.status(204).send();
	} catch (err) {
		console.error(err);
		res.status(400).json({ error: "Failed to delete WorkoutSession." });
	}
});


workoutSessionRouter.post("/from-template/:templateId", authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
	const templateId = req.params.templateId;
	const userId = req.userId;

	try {
		// 1. Fetch workout template with exercises
		const template = await prisma.workoutTemplate.findUnique({
			where: { id: templateId },
			include: {
				exercises: true,
			},
		});

		if (!template){
			res.status(404).json({ error: "Workout template not found." });
			return;
		}

		// 2. Create WorkoutSession
		if (!userId) {
			res.status(400).json({ error: "User ID is required." });
			return;
		}
		const workoutSession = await prisma.workoutSession.create({
			data: {
				userId,
				workoutTemplateId: templateId
			}
		});
		
		// 3. Create Exercise Sessions
		const exerciseSessionCreates = template.exercises.map(exercise => {
			return prisma.exerciseSession.create({
				data: {
					userId,
					exerciseTemplateID: exercise.id,
					workoutSessionId: workoutSession.id,
					sets: {
						create: []
					},
				},
			})
		});

		await Promise.all(exerciseSessionCreates);

		// Return the workout session ID so that the frontend can navigate to it
		return res.status(201).json({ workoutSessionId: workoutSession.id });
	} catch (err: any) {
		res.status(400).json({ error: err.message });
	}
});

export default workoutSessionRouter;
