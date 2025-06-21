import { Router, Request, Response } from "express";
import prisma from "../lib/prisma"; // import prisma client to communicate with the db

const templateRouter = Router();

// add endpoints
templateRouter.get('/', (req: Request, res: Response) => {
    // const templates = await prisma.template.findMany();
    res.status(200).json({
        msg: "Hi from template router"
    });
});

export default templateRouter;