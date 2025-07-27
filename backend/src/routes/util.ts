import { Router, Request, Response } from "express";
import prisma from "../lib/prisma";
import { AuthRequest } from "../lib/types";
import authMiddleware from "../middleware/authMiddleware";
import { subDays, format, isToday } from 'date-fns';

export const utilRouter = Router();

utilRouter.get('/', (req: Request, res: Response) => {
    res.status(200).json({
        msg: "Util Router"
    });
})

utilRouter.get('/get-streaks', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const sessions = await prisma.exerciseSession.findMany({
      where: {
        userId: req.userId,
        sets: { some: {} }
      },
      select: { date: true }
    });

    // Build a Set of YYYY-MM-DD strings
    const sessionDates = new Set(
      sessions.map((s) => format(new Date(s.date), 'yyyy-MM-dd'))
    );

    let streak = 0;
    let currentDate = new Date(); // today

    while (true) {
      const dateString = format(currentDate, 'yyyy-MM-dd');
      if (sessionDates.has(dateString)) {
        streak += 1;
        currentDate = subDays(currentDate, 1); // go back one day
      } else {
        break; // streak is broken
      }
    }

    res.status(200).json({ currentStreak: streak });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});
