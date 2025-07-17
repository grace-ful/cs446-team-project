import { Router, Response, Request } from "express";
import { getMessagesFromDb, saveMessageToDb } from "../chat/chatController";
import authMiddleware from "../middleware/authMiddleware";
import { AuthRequest } from "../lib/types";

export const chatRouter = Router();

//#region PUBLIC ENDPOINTS
chatRouter.get('/', (req: Request, res: Response) => {
    res.status(200).json({
        msg: "Hi from chat router"
    });
});

//#endregion

//#region PROTECTED ENDPOINTS
chatRouter.get("/history/:userB", authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
  const userA = req.userId;
  const {userB} = req.params;
  if (!userA || !userB) {
    return res.status(400).json({ error: "userA and userB are required" });
  }
  try {
    const messages = await getMessagesFromDb(userA as string, userB as string);
    res.json(messages);
  } catch (e) {
    res.status(500).json({ error: "Failed to fetch messages" });
  }
});

chatRouter.post("/send", authMiddleware, async (req: AuthRequest, res: Response): Promise<any> => {
    const senderId = req.userId;
  const { receiverId, content } = req.body;
  if (!senderId || !receiverId || !content) {
    return res.status(400).json({ error: "senderId, receiverId, and text are required" });
  }
  try {
    const saved = await saveMessageToDb({ senderId, receiverId, content });
    res.status(201).json(saved);
  } catch (e) {
    res.status(500).json({ error: "Failed to send message" });
  }
});

//#endregion