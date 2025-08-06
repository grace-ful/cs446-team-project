import { Request, Response, NextFunction } from "express";

export default function adminMiddleware(req: Request, res: Response, next: NextFunction): void {
    const adminToken = req.headers['x-admin-token'];

    if (adminToken !== process.env.ADMIN_SECRET) {
        res.status(403).json({ message: "Forbidden: Invalid admin token" });
        return;
    }

    console.log("Admin middleware validated request.");
    next();
}