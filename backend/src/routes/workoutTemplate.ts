import { Router, Request, Response } from "express";
import prisma from "../lib/prisma"; // import prisma client to communicate with the db
import authMiddleware from "../middleware/authMiddleware";
import { AuthRequest } from "src/lib/types";

const workoutTemplateRouter = Router();

// #region PUBLIC ENDPOINTS

// dummy endpoint
workoutTemplateRouter.get('/', (req: Request, res: Response) => {
    // const workouts = await prisma.workout.findMany();
    res.status(200).json({
        msg: "Hi from workout router"
    });
});

// get all generic workouts
workoutTemplateRouter.get('/general', async (req: Request, res: Response): Promise<any> => {
  try {
    const templates = await prisma.workoutTemplate.findMany({
      where: { isGeneral: true },
      include: {
        exercises: true,
      },
    });
    res.json(templates);
  } catch (error){
    res.status(500).json({ error: 'Failed to fetch general workout templates' });
  }
});

//#endregion

//#region PROTECTED ENDPOINTS

workoutTemplateRouter.get('/by-user/:userId', authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
    const userId = req.userId;

    try {
      const templates = await prisma.workoutTemplate.findMany({
        where: { userId },
        include: {
          exercises: true,
        },
      });
      res.json(templates);
    } catch (error) {
      res.status(500).json({ error: 'Failed to fetch user\'s workout templates' });
    }
});

workoutTemplateRouter.post('/add', authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
  try {
    const { name, isGeneral, exerciseIds } = req.body;
    const userId = req.userId;

    // Validate required fields
    if (!name || !Array.isArray(exerciseIds) || exerciseIds.length === 0) {
      return res.status(400).json({ error: 'Name and exerciseIds are required' });
    }

    // Create WorkoutTemplate and connect exercises by ID
    const newTemplate = await prisma.workoutTemplate.create({
      data: {
        name,
        isGeneral: isGeneral ?? false,
        userId,
        exercises: {
          connect: exerciseIds.map((id: string) => ({ id })),
        },
      },
      include: {
        exercises: true,
      },
    });

    res.status(201).json(newTemplate);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to create workout template' });
  }
});

workoutTemplateRouter.put('/:id/add-exercises', authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
  const { id } = req.params;
  const { exerciseIds } = req.body;
  const userId = req.userId;

  // Validate input
  if (!Array.isArray(exerciseIds) || exerciseIds.length === 0) {
    return res.status(400).json({ error: 'exerciseIds must be a non-empty array' });
  }

  try {
    const updatedTemplate = await prisma.workoutTemplate.update({
      where: { id },
      data: {
        exercises: {
          connect: exerciseIds.map((eid: string) => ({ id: eid }))
        }
      },
      include: {
        exercises: true
      }
    });

    res.status(200).json(updatedTemplate);
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Failed to add exercises to workout template' });
  }
});

workoutTemplateRouter.delete('/:id/remove-exercise', authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
  const { id } = req.params;
  const { exerciseId } = req.body;
  const userId = req.userId;

  // Validate input
  if (!exerciseId) {
    return res.status(400).json({ error: 'exerciseId is required' });
  }

  try {
    const updatedTemplate = await prisma.workoutTemplate.update({
      where: { id },
      data: {
        exercises: {
          disconnect: { id: exerciseId }
        }
      },
      include: {
        exercises: true
      }
    });

    res.status(200).json(updatedTemplate);
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Failed to remove exercise from workout template' });
  }
});

//#endregion

export default workoutTemplateRouter;