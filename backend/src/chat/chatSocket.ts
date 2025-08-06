// src/chat/chatSocket.ts

import { Server, Socket } from "socket.io";
import { saveMessageToDb, getMessagesFromDb } from "./chatController"; // You will implement these!
import { ChatMessage } from "src/lib/types";

export function setupChatSocket(io: Server) {
  io.on("connection", (socket: Socket) => {
    console.log("User connected:", socket.id);

    // Join a personal room for 1-on-1 chat
    socket.on("join", (userId: number) => {
      socket.join(userId.toString());
      console.log(`User ${userId} joined their chat room`);
    });

    // Send a message to another user
    socket.on("send_message", async (msg: ChatMessage) => {
      try {
        // 1. Save to DB
        const savedMessage = await saveMessageToDb(msg);

        // 2. Emit to receiver's room
        io.to(msg.receiverId.toString()).emit("receive_message", savedMessage);

        // 3. (Optional) Emit to sender too for immediate UI update
        socket.emit("receive_message", savedMessage);

        console.log(
          `Message from ${msg.senderId} to ${msg.receiverId}: ${msg.content}`
        );
      } catch (error) {
        console.error("Error handling send_message:", error);
        socket.emit("error", "Failed to send message");
      }
    });

    // (Optional) Fetch chat history between two users
    socket.on("fetch_history", async (data: { userA: string; userB: string }) => {
      try {
        const messages = await getMessagesFromDb(data.userA, data.userB);
        socket.emit("chat_history", messages);
      } catch (error) {
        console.error("Error fetching history:", error);
        socket.emit("error", "Failed to fetch chat history");
      }
    });

    socket.on("disconnect", () => {
      console.log("User disconnected:", socket.id);
    });
  });
}
