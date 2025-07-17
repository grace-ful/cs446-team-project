import { Router, Request, Response } from "express";
import prisma from "../lib/prisma";
import { TimePreference, ExperienceLevel, GymFrequency, AuthRequest, Equipment } from "../lib/types";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import authMiddleware from "../middleware/authMiddleware";

const userRouter = Router();

const SALT_ROUNDS = 10;

function generateToken(userId: string){
	return jwt.sign({ userId }, process.env.JWT_SECRET!, { expiresIn: '7d' });
}

// #region PUBLIC ENDPOINTS

userRouter.get("/", (req: Request, res: Response) => {
	res.status(200).json({
		msg: "Hi from user router",
	});
});

// Create new user
userRouter.post("/signup", async (req: Request, res: Response): Promise<any> => {
	const {
		email,
		name,
		password,
		heightCm,
		weightKg,
		age,
		location,
		timePreference,
		experienceLevel,
		gymFrequency,
	} = req.body;

	// Validate enums
	if (!Object.values(TimePreference).includes(timePreference)) {
		return res.status(400).json({
			error: `Invalid timePreference. Must be one of: ${Object.values(
				TimePreference
			).join(", ")}.`,
		});
	}

	if (!Object.values(ExperienceLevel).includes(experienceLevel)) {
		return res.status(400).json({
			error: `Invalid experienceLevel. Must be one of: ${Object.values(
				ExperienceLevel
			).join(", ")}.`,
		});
	}

	if (!Object.values(GymFrequency).includes(gymFrequency)) {
		return res.status(400).json({
			error: `Invalid gymFrequency. Must be one of: ${Object.values(
				GymFrequency
			).join(", ")}.`,
		});
	}

	try {
		const existingUser = await prisma.user.findUnique({
			where: { email },
		});

		if (existingUser) {
			return res.status(400).json({ error: "Email already in use." });
		}

		const hashedPassword = await bcrypt.hash(password, SALT_ROUNDS);
		const newUser = await prisma.user.create({
			data: {
				email,
				name,
				passwordHash: hashedPassword,
				heightCm,
				weightKg,
				age,
				location,
				timePreference: timePreference as TimePreference,
				experienceLevel: experienceLevel as ExperienceLevel,
				gymFrequency: gymFrequency as GymFrequency,
			},
		});

		const token = generateToken(newUser.id);

		const { passwordHash: _, ...userWithoutPassword } = newUser;
		res.status(201).json({
			user: userWithoutPassword,
			token: `Bearer ${token}`,
		});
	} catch (err: any) {
		res.status(400).json({ error: err.message });
	}
});

userRouter.post("/login", async (req: Request, res: Response): Promise<any> => {
	const { email, password } = req.body;

	if (!email || !password) {
		return res.status(400).json({ error: "Email and password are required." });
	}

	try {
		const user = await prisma.user.findUnique({
			where: { email },
		});

		if (!user) {
			return res.status(401).json({ error: "Invalid credentials." });
		}

		const passwordMatch = await bcrypt.compare(password, user.passwordHash);

		if (!passwordMatch) {
			return res.status(401).json({ error: "Invalid credentials." });
		}

		const token = generateToken(user.id);

		const { passwordHash, ...userWithoutPassword } = user;
		res.status(200).json({
			id: user.id,
			user: userWithoutPassword,
			token: `Bearer ${token}`,
		});
	} catch (err: any) {
		res.status(500).json({ error: err.message });
	}
});

// #endregion

// #region PROTECTED ENDPOINTS

userRouter.get("/users", authMiddleware, async (req: AuthRequest, res: Response) => {
	const userId = req.userId;
	try {
		const users = await prisma.user.findMany();
		res.status(200).json(users);
	} catch (err: any) {
		res.status(500).json({ error: err.message });
	}
});

// Get user by ID
userRouter.get("/:id", authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
	const userId = req.userId;

	try {
		const user = await prisma.user.findUnique({
			where: { id: userId },
		});

		if (!user) {
			return res.status(404).json({ error: "User not found." });
		}

		res.json(user);
	} catch (err: any) {
		res.status(500).json({ error: err.message });
	}
});

// Update user
userRouter.put("/:id", authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
	const userId = req.userId;
	const {
		name,
		age,
		heightCm,
		weightKg,
		location,
		timePreference,
		experienceLevel,
		gymFrequency,
		password,
	} = req.body;

	const updateData: any = {};

	if (name != null) updateData.name = name;
	if (age != null) updateData.age = age;
	if (heightCm != null) updateData.heightCm = heightCm;
	if (weightKg != null) updateData.weightKg = weightKg;
	if (location != null) updateData.location = location;

	if (timePreference != null) {
		if (!Object.values(TimePreference).includes(timePreference)) {
			return res.status(400).json({ error: "Invalid timePreference" });
		}
		updateData.timePreference = timePreference;
	}

	if (experienceLevel != null) {
		if (!Object.values(ExperienceLevel).includes(experienceLevel)) {
			return res.status(400).json({ error: "Invalid experienceLevel" });
		}
		updateData.experienceLevel = experienceLevel;
	}

	if (gymFrequency != null) {
		if (!Object.values(GymFrequency).includes(gymFrequency)) {
			return res.status(400).json({ error: "Invalid gymFrequency" });
		}
		updateData.gymFrequency = gymFrequency;
	}

	if (password != null) {
		const hashedPassword = await bcrypt.hash(password, SALT_ROUNDS);
		updateData.passwordHash = hashedPassword;
	}

	try {
		const updatedUser = await prisma.user.update({
			where: { id: userId },
			data: updateData,
		});
		res.json(updatedUser);
	} catch (err: any) {
		console.error("❌ Update error:", err);
		res.status(400).json({ error: err.message });
	}
});

// Delete user
userRouter.delete("/:id", authMiddleware, async (req: AuthRequest, res: Response) => {
	const userId = req.userId;

	try {
		await prisma.user.delete({
			where: { id: userId },
		});

		res.status(204).send(); // success with no content
	} catch (err: any) {
		res.status(400).json({ error: err.message });
	}
});

// #endregion

export default userRouter;
