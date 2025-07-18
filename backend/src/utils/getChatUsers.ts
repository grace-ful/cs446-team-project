import prisma from "../lib/prisma";

export async function getChatUsersForUser(userId: string) {
  // Get distinct user IDs this user has chatted with (as sender or receiver)
  const sent = await prisma.message.findMany({
    where: { senderId: userId },
    select: { receiverId: true },
  });
  const received = await prisma.message.findMany({
    where: { receiverId: userId },
    select: { senderId: true },
  });

  // Flatten and deduplicate user IDs
  const userSet = new Set([
    ...sent.map((msg) => msg.receiverId),
    ...received.map((msg) => msg.senderId),
  ]);
  userSet.delete(userId); // Remove self if present
  const userIds = Array.from(userSet);

  if (userIds.length === 0) return [];

  // Fetch only id and name for these users
  const users = await prisma.user.findMany({
    where: { id: { in: userIds } },
    select: {
      id: true,
      name: true,
    },
  });

  return users;
}
