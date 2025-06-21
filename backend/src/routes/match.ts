import { Router, Request, Response } from "express";
import prisma from "../lib/prisma"; // import prisma client to communicate with the db

const matchRouter = Router();

// add endpoints
matchRouter.get('/', (req: Request, res: Response) => {
    // const matches = await prisma.match.findMany();
    res.status(200).json({
        msg: "Hi from profile matching router"
    });
});

export default matchRouter;