import { Request, Response, Router } from "express";
import prisma from "../lib/prisma";
import { AuthRequest } from "src/lib/types";
import authMiddleware from "../middleware/authMiddleware";
import { calculateMatchScore } from "../utils/calculateMatchScore";

const matchesRouter = Router();


// #region PUBLIC ENDPOINTS
matchesRouter.get("/", (req: Request, res: Response) => {
    res.status(200).json({
        msg: "Hey there from the matches router!"
    })
});

//#endregion

//#region PROTECTED ENDPOINTS
matchesRouter.post("/update/:userId", authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
    const userId = req.userId;
    try {
    // 1. Fetch current user
    const currentUser = await prisma.user.findUnique({
      where: { id: userId },
    });

    if (!currentUser) {
      return res.status(404).json({ error: "User not found" });
    }

    // 2. Fetch all other users
    const otherUsers = await prisma.user.findMany({
      where: { id: { not: userId } },
    });

    // 3. Calculate scores for all other users
    const scoredMatches = otherUsers.map((other) => ({
      userId: currentUser.id,
      matchedUserId: other.id,
      score: calculateMatchScore(currentUser, other),
    }));

    // 4. Sort and keep top 10
    const topMatches = scoredMatches
      .sort((a, b) => b.score - a.score)
      .slice(0, 10);

    // 5. Delete existing matches
    await prisma.matchEntry.deleteMany({
      where: { userId },
    });

    // 6. Create new top 10
    await prisma.matchEntry.createMany({
      data: topMatches,
    });

    return res.json({ message: "Top 10 matches updated.", topMatches });
  } catch (error) {
    console.error("Failed to update matches:", error);
    return res.status(500).json({ error: "Something went wrong." });
  }
});

matchesRouter.get('/by-user/:userId', authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
    const userId = req.userId;

    try {
    const matches = await prisma.matchEntry.findMany({
      where: { userId },
      orderBy: { score: "desc" },
      include: {
        matchee: true, // Includes full matched user info
      },
    });

    return res.json(matches);
  } catch (error) {
    console.error("Failed to fetch matches:", error);
    return res.status(500).json({ error: "Could not fetch matches." });
  }
});

matchesRouter.post('/refresh-all', authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
    try {
    // 1. Get all users
    const allUsers = await prisma.user.findMany();

    // 2. For each user, calculate and store their top K matches
    for (const user of allUsers) {
      const others = allUsers.filter(u => u.id !== user.id);

      const topKMatches = others
        .map(other => ({
          userId: user.id,
          matchedUserId: other.id,
          score: calculateMatchScore(user, other),
        }))
        .sort((a, b) => b.score - a.score)
        .slice(0, 10);

      // Clear and insert
      await prisma.matchEntry.deleteMany({ where: { userId: user.id } });
      await prisma.matchEntry.createMany({ data: topKMatches });
    }

    return res.json({ message: "All match entries refreshed." });
  } catch (error) {
    console.error("Failed to refresh all matches:", error);
    return res.status(500).json({ error: "Refresh failed." });
  }
});

//#endregion

export default matchesRouter;