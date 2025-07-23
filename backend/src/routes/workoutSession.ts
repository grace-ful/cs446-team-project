import { Router, Request, Response } from "express";
import prisma from "../lib/prisma";
import authMiddleware from "../middleware/authMiddleware";
import { AuthRequest } from "../lib/types";

const workoutSessionRouter = Router();

// Create a WorkoutSession
workoutSessionRouter.post("/", authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
	const userId = req.userId;
	const { workoutTemplateId, workoutDate, notes } = req.body;

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
	authMiddleware,
	async (req: AuthRequest, res: Response): Promise<any> => {
		const userId = req.userId;
		try {
			const session = await prisma.workoutSession.findUnique({
				where: { id: req.params.id, userId },
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
workoutSessionRouter.get("/by-user/:userId", authMiddleware, async (req: AuthRequest, res: Response) => {
	const userId = req.userId;
	try {
		const sessions = await prisma.workoutSession.findMany({
			where: { userId },
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
workoutSessionRouter.put("/:id", authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
  const { notes, workoutDate, exerciseSessions } = req.body;
  const userId = req.userId;
  const sessionId = req.params.id;

  try {
    // Step 0: Verify the WorkoutSession belongs to the user
    const session = await prisma.workoutSession.findUnique({
      where: { id: sessionId },
      select: { userId: true },
    });

    if (!session || session.userId !== userId) {
      return res.status(403).json({ error: "Not authorized to update this session" });
    }

    // Step 1: Update basic workout session fields
    await prisma.workoutSession.update({
      where: { id: sessionId },
      data: {
        notes,
        workoutDate: workoutDate ? new Date(workoutDate) : undefined,
      },
    });

    // Step 2: Validate exerciseSessions ownership
    const validSessions = await prisma.exerciseSession.findMany({
      where: {
        id: { in: exerciseSessions.map((s: any) => s.id) },
        userId,
      },
      select: { id: true },
    });

    const validSessionIds = new Set(validSessions.map(s => s.id));

    // Step 3: Loop through sessions & sets
    for (const session of exerciseSessions) {
      const exerciseSessionId = session.id;

      if (!validSessionIds.has(exerciseSessionId)) continue;

      for (const set of session.sets) {
        if (set.id) {
          // Update existing set
          await prisma.exerciseSet.update({
            where: { id: set.id },
            data: {
              reps: set.reps,
              weight: set.weight,
              duration: set.duration,
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

    res.status(200).json({ msg: "WorkoutSession updated successfully" });
  } catch (err: any) {
    console.error(err);
    res.status(400).json({ error: "Failed to update WorkoutSession." });
  }
});



// Delete session
workoutSessionRouter.delete("/:id", authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
  const sessionId = req.params.id;
  const userId = req.userId;

  try {
    // Step 1: Verify ownership of the session
    const session = await prisma.workoutSession.findUnique({
      where: { id: sessionId },
      select: { userId: true },
    });

    if (!session || session.userId !== userId) {
      return res.status(403).json({ error: "Not authorized to delete this session" });
    }

    // Step 2: Delete the session (cascade will handle dependencies)
    await prisma.workoutSession.delete({
      where: { id: sessionId },
    });

    res.status(204).json({
		msg: "WorkoutSession deleted successfully"
	});
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
