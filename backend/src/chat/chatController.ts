import { ChatMessage } from "src/lib/types";
import prisma from "../lib/prisma"; // Adjust path if your prisma client is elsewhere

/**
 * Save a new chat message to the database.
 * Returns the saved message, including timestamp and id.
 */
export async function saveMessageToDb(msg: ChatMessage): Promise<ChatMessage> {
    console.log("Ran this!")
  const saved = await prisma.message.create({
    data: {
        senderId: msg.senderId,
        receiverId: msg.receiverId,
        content: msg.content,
    },
  });
  return {
    senderId: saved.senderId,
    receiverId: saved.receiverId,
    content: saved.content,
  };
}

/**
 * Get all messages between two users, ordered by timestamp ascending.
 * Returns an array of chat messages.
 */
export async function getMessagesFromDb(userA: string, userB: string): Promise<ChatMessage[]> {
  const messages = await prisma.message.findMany({
    where: {
      OR: [
        { senderId: userA, receiverId: userB },
        { senderId: userB, receiverId: userA },
      ],
    },
    orderBy: { createdAt: "asc" },
  });

  return messages.map((msg) => ({
    id: msg.id,
    senderId: msg.senderId,
    receiverId: msg.receiverId,
    content: msg.content,
    createdAt: msg.createdAt.toISOString(),
  }));
}
