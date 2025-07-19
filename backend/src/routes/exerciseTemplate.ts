import { Router, Request, Response } from "express";
import prisma from "../lib/prisma";
import { MuscleGroup, BodyPart, AuthRequest, Equipment } from "../lib/types";
import authMiddleware from "../middleware/authMiddleware";

const exerciseTemplateRouter = Router();

// #region PUBLIC ENDPOINTS

// GET general templates (isGeneral = true)
exerciseTemplateRouter.get("/general", async (_req: Request, res: Response) => {
	try {
		const templates = await prisma.exerciseTemplate.findMany({
			where: { isGeneral: true },
		});
		res.json(templates);
	} catch (error) {
		res.status(500).json({ error: "Failed to fetch general exercise templates." });
	}
});

// GET template by id
exerciseTemplateRouter.get(
	"/:id",
	async (req: Request, res: Response): Promise<any> => {
		try {
			const template = await prisma.exerciseTemplate.findUnique({
				where: { id: req.params.id },
			});

			if (!template) {
				return res.status(404).json({ error: "Template not found." });
			}

			res.json(template);
		} catch (error) {
			res.status(500).json({ error: "Failed to fetch template." });
		}
	}
);

// #endregion

// #region PROTECTED ENDPOINTS

// GET templates by user ID
exerciseTemplateRouter.get(
	"/by-user/:userId",
	authMiddleware,
	async (req: AuthRequest, res: Response): Promise<any> => {
		const userId = req.userId;
		console.log(req.body);
		console.log(`userId = ${userId}`);

		try {
			const templates = await prisma.exerciseTemplate.findMany({
				where: { userId },
			});
			res.json(templates);
		} catch (error) {
			res.status(500).json({ error: "Failed to fetch user's exercise templates." });
		}
	}
);

// Create new Exercise Template
exerciseTemplateRouter.post(
	"/",
	authMiddleware,
	async (req: AuthRequest, res: Response): Promise<any> => {
		console.log(req.body);
		const userId = req.userId;
		const { name, muscleGroup, bodyPart, isGeneral, imageURL, equipment, description } = req.body;
		console.log(`userId = ${userId}`);

		if (!name || !muscleGroup || !bodyPart || isGeneral === undefined) {
			return res.status(400).json({ error: "Missing required fields." });
		}

		// Enum validation
		if (!Object.values(MuscleGroup).includes(muscleGroup)) {
			return res.status(400).json({ error: "Invalid muscleGroup value." });
		}

		if (!Object.values(BodyPart).includes(bodyPart)) {
			return res.status(400).json({ error: "Invalid bodyPart value." });
		}

		if (!Object.values(Equipment).includes(equipment)){
				return res.status(400).json({
					error: `Invalid equipment. Must be one of: ${Object.values(
						Equipment
					).join(", ")}.`,
				});
			}

		try {
			const newExercise = await prisma.exerciseTemplate.create({
				data: {
					name,
					muscleGroup,
					bodyPart,
					isGeneral,
					imageURL,
					userId,
					equipment,
					description
				},
			});

			res.status(201).json(newExercise);
		} catch (err: any) {
			res.status(400).json({ error: err.message });
		}
	}
);

// UPDATE a template
exerciseTemplateRouter.put("/:id", authMiddleware, async (req: AuthRequest, res: Response) => {
	const { name, muscleGroup, bodyPart, imageURL, isGeneral, equipment, description } = req.body;

	const userId = req.userId;

	try {
		const updated = await prisma.exerciseTemplate.update({
			where: { id: req.params.id },
			data: {
				name,
				muscleGroup,
				bodyPart,
				imageURL,
				isGeneral,
				equipment,
				description
			},
		});

		res.json(updated);
	} catch (error) {
		res.status(400).json({ error: "Failed to update template." });
	}
});

// DELETE a template
exerciseTemplateRouter.delete("/:id", authMiddleware, async (req: AuthRequest, res: Response) => {
	const userId = req.userId;
	try {
		await prisma.exerciseTemplate.delete({
			where: { id: req.params.id },
		});

		res.status(204).send();
	} catch (error) {
		res.status(400).json({ error: "Failed to delete template." });
	}
});

// #endregion

export default exerciseTemplateRouter;