import { saveMessageToDb, getMessagesFromDb } from "../chat/chatController";

(async () => {
  await saveMessageToDb({
    senderId: "096c2bd1-e3b5-4a88-bab3-176f60d18607",
    receiverId: "2dda7058-b50d-4da7-93a6-b57d092818b5",
    content: "Hello from Max to Charles!",
  });

  const history = await getMessagesFromDb("USER_ID_1", "USER_ID_2");
  console.log(history);
})();