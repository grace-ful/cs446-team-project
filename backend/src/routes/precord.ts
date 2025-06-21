import { Router, Request, Response } from "express";
import prisma from "../lib/prisma"; // import prisma client to communicate with the db

const precordRouter = Router();

// add endpoints
precordRouter.get('/', (req: Request, res: Response) => {
    // const precords = await prisma.pr.findMany();
    res.status(200).json({
        msg: "Hi from PR router"
    });
});

export default precordRouter;