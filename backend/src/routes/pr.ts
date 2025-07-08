import { Router, Request, Response } from "express";
import prisma from "../lib/prisma";

const prRouter = Router();

// Create/set a PR
prRouter.post("/", async (req: Request, res: Response): Promise<any> => {
	const { userId, templateId, weight, duration, date } = req.body;

	if (!userId || !templateId || !date) {
		return res
			.status(400)
			.json({ error: "userId, templateId, and date are required." });
	}

	try {
		const newPR = await prisma.pR.create({
			data: {
				userId,
				templateId,
				weight,
				duration,
				date: new Date(date),
			},
		});

		res.status(201).json(newPR);
	} catch (err: any) {
		res.status(400).json({ error: err.message });
	}
});

// Get PRs by user
prRouter.get("/by-user/:userId", async (req: Request, res: Response) => {
	const { userId } = req.params;

	try {
		const prs = await prisma.pR.findMany({
			where: { userId },
			include: {
				template: true,
			},
			orderBy: {
				date: "desc",
			},
		});

		res.status(200).json(prs);
	} catch (err: any) {
		res.status(500).json({ error: err.message });
	}
});

// Get PR for a user and specific exercise
prRouter.get(
	"/by-user/:userId/by-exercise/:templateId",
	async (req: Request, res: Response): Promise<any> => {
		const { userId, templateId } = req.params;

		try {
			const pr = await prisma.pR.findFirst({
				where: {
					userId,
					templateId,
				},
				orderBy: {
					date: "desc",
				},
			});

			if (!pr) {
				return res
					.status(404)
					.json({ error: "No PR found for this user and exercise." });
			}

			res.status(200).json(pr);
		} catch (err: any) {
			res.status(500).json({ error: err.message });
		}
	}
);

// Update a PR
prRouter.put("/:id", async (req: Request, res: Response) => {
	const { weight, duration, date } = req.body;

	try {
		const updated = await prisma.pR.update({
			where: { id: req.params.id },
			data: {
				weight,
				duration,
				date: date ? new Date(date) : undefined,
			},
		});

		res.status(200).json(updated);
	} catch (err: any) {
		res.status(400).json({ error: "Failed to update PR." });
	}
});

// Delete a PR
prRouter.delete("/:id", async (req: Request, res: Response) => {
	try {
		await prisma.pR.delete({ where: { id: req.params.id } });
		res.status(204).send();
	} catch (err: any) {
		res.status(400).json({ error: "Failed to delete PR." });
	}
});

export default prRouter;
