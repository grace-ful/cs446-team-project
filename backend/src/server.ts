import { createServer } from "http";
import { app } from "./app";
import { Server } from "socket.io";
import { setupChatSocket } from "./chat/chatSocket";

const PORT = process.env.PORT || 3000;

const httpServer = createServer(app);

const io = new Server(httpServer, {
    cors: {
    origin: "*",
  },
});

setupChatSocket(io);

httpServer.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
    console.log(`Socket.io server is ready on port ${PORT}`);
});