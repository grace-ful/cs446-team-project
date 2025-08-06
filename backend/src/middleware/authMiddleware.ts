import { NextFunction, Request, Response } from "express";
import jwt from "jsonwebtoken"
import { AuthRequest } from "src/lib/types";

export default function authMiddleware(req: AuthRequest, res: Response, next: NextFunction): void {
    const authHeader = req.headers.authorization;
    const token = authHeader?.split(' ')[1];

    console.log(`Request was sent with token = ${token}`);

    if (!token){
        res.status(401).json({ message: "Unauthorized: No token provided" });
        return;
    }

    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET!) as { userId: string };
        req.userId = decoded.userId;
        console.log("Middleware ran successfully");
        next();
    } catch (err) {
        res.status(401).json({ message: "Unauthorized: Invalid token" });
        return;
    }
}