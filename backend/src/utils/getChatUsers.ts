import prisma from "../lib/prisma";

export async function getChatUsersForUser(userId: string) {
  // Get all messages involving this user, sorted by newest first
  const messages = await prisma.message.findMany({
    where: {
      OR: [
        { senderId: userId },
        { receiverId: userId },
      ],
    },
    orderBy: {
      createdAt: "desc",
    },
    select: {
      senderId: true,
      receiverId: true,
      createdAt: true,
    },
  });

  const seen = new Set<string>();
  const userToTimestamp: { [id: string]: Date } = {};

  for (const msg of messages) {
    const otherUserId = msg.senderId === userId ? msg.receiverId : msg.senderId;
    if (!seen.has(otherUserId)) {
      seen.add(otherUserId);
      userToTimestamp[otherUserId] = msg.createdAt;
    }
  }

  const userIds = Array.from(seen);

  if (userIds.length === 0) return [];

  // Fetch the corresponding user info
  const users = await prisma.user.findMany({
    where: { id: { in: userIds } },
    select: {
      id: true,
      name: true,
    },
  });

  // Combine with last message time and sort descending
  const enriched = users.map((u) => ({
    ...u,
    lastMessageAt: userToTimestamp[u.id],
  }));

  return enriched.sort((a, b) => b.lastMessageAt.getTime() - a.lastMessageAt.getTime());
}
