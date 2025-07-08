// Create a server - Yash
import express, { Request, Response } from "express";
import cors from "cors";
import apiRouter from "./routes";
import dotenv from "dotenv";

dotenv.config();

const app = express();
const PORT = process.env.PORT;

app.use(cors());
app.use(express.json());

// adding api router
app.use("/api", apiRouter);

app.get("/", (req: Request, res: Response) => {
	res.status(200).json({
		msg: "App is up!",
	});
});

app.listen(PORT, () => {
	console.log(`Listening on port ${PORT}`);
});
