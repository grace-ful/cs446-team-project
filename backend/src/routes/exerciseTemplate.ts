import { Router, Request, Response } from "express";
import prisma from "../lib/prisma";
import { MuscleGroup, BodyPart } from "../lib/types";

const exerciseTemplateRouter = Router();

// GET templates by user ID
exerciseTemplateRouter.get(
	"/by-user/:userId",
	async (req: Request, res: Response) => {
		const { userId } = req.params;

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

// GET one template by ID
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

// Create new Exercise Template
exerciseTemplateRouter.post(
	"/",
	async (req: Request, res: Response): Promise<any> => {
		const { name, muscleGroup, bodyPart, isGeneral, imageURL, userId, equipment } = req.body;

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
				},
			});

			res.status(201).json(newExercise);
		} catch (err: any) {
			res.status(400).json({ error: err.message });
		}
	}
);

// UPDATE a template
exerciseTemplateRouter.put("/:id", async (req: Request, res: Response) => {
	const { name, muscleGroup, bodyPart, imageURL, isGeneral, equipment } = req.body;

	try {
		const updated = await prisma.exerciseTemplate.update({
			where: { id: req.params.id },
			data: {
				name,
				muscleGroup,
				bodyPart,
				imageURL,
				isGeneral,
				equipment
			},
		});

		res.json(updated);
	} catch (error) {
		res.status(400).json({ error: "Failed to update template." });
	}
});

// DELETE a template
exerciseTemplateRouter.delete("/:id", async (req: Request, res: Response) => {
	try {
		await prisma.exerciseTemplate.delete({
			where: { id: req.params.id },
		});

		res.status(204).send();
	} catch (error) {
		res.status(400).json({ error: "Failed to delete template." });
	}
});

export default exerciseTemplateRouter;
