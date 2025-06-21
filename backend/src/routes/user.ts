import { Router, Request, Response } from "express";
import prisma from "../lib/prisma"; // import prisma client to communicate with the db

const userRouter = Router();

// add endpoints
userRouter.get('/', (req: Request, res: Response) => {
    // const users = await prisma.user.findMany();
    res.status(200).json({
        msg: "Hi from user router"
    });
});

export default userRouter;