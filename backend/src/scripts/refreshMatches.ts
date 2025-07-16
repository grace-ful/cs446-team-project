// scripts/refreshMatches.ts
import axios from "axios";
import dotenv from "dotenv";
dotenv.config();

const ADMIN_TOKEN = process.env.ADMIN_SECRET!;
const API_URL = "https://cs446-team-project-production.up.railway.app/api/matches/refresh-all";
// const API_URL = "http://localhost:3000/api/matches/refresh-all";

async function refreshAllMatches() {
  try {
    const response = await axios.post(API_URL, {}, {
      headers: {
        "x-admin-token": ADMIN_TOKEN,
      },
    });

    console.log("✅ Matches refreshed:", response.data);
  } catch (error: any) {
    console.error("❌ Failed to refresh matches:", error.response?.data || error.message);
  }
}

refreshAllMatches();
