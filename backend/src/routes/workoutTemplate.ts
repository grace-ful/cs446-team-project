import { Router, Request, Response } from "express";
import prisma from "../lib/prisma"; // import prisma client to communicate with the db

const workoutTemplateRouter = Router();

// add endpoints
workoutTemplateRouter.get('/', (req: Request, res: Response) => {
    // const workouts = await prisma.workout.findMany();
    res.status(200).json({
        msg: "Hi from workout router"
    });
});

workoutTemplateRouter.post('/add', async (req: Request, res: Response): Promise<any> => {
  try {
    const { name, isGeneral, userId, exerciseIds } = req.body;

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

workoutTemplateRouter.put('/:id/add-exercises', async (req: Request, res: Response): Promise<any> => {
  const { id } = req.params;
  const { exerciseIds } = req.body;

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

workoutTemplateRouter.put('/:id/remove-exercise', async (req: Request, res: Response): Promise<any> => {
  const { id } = req.params;
  const { exerciseId } = req.body;

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



export default workoutTemplateRouter;