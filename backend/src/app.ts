// Create a server - Yash
import express, { Request, Response } from "express";
import cors from "cors";
import apiRouter from "./routes";
import dotenv from "dotenv";

dotenv.config();

export const app = express();
const PORT = parseInt(process.env.PORT || "3000", 10);

app.use(cors());
app.use(express.json());

// adding api router
app.use("/api", apiRouter);

app.get("/", (req: Request, res: Response) => {
	res.status(200).json({
		msg: "Welcome to the CS446 Fit4Me Backend Server",
	});
});

app.get('/api/test', (req, res) => {
  res.json({ status: 'ok' });
});

app.listen(PORT, '0.0.0.0', () => {
	console.log(`Listening on port ${PORT}`);
});
