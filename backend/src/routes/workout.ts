import { Router, Request, Response } from "express";
import prisma from "../lib/prisma"; // import prisma client to communicate with the db

const workoutRouter = Router();

// add endpoints
workoutRouter.get('/', (req: Request, res: Response) => {
    // const workouts = await prisma.workout.findMany();
    res.status(200).json({
        msg: "Hi from workout router"
    });
});

export default workoutRouter;