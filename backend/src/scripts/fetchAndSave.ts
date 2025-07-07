import axios from "axios";
import dotenv from "dotenv";
import path from "path";
import fs from "fs";
import { MuscleGroup } from "@prisma/client";

dotenv.config();

// function cleanText(html: string): string {
//   return html
//     .replace(/<[^>]*>/g, '')    // Remove HTML tags
//     .replace(/\n/g, ' ')        // Replace newline chars
//     .replace(/\s+/g, ' ')       // Collapse extra spaces
//     .trim();
// }

type WgerExerciseResponse = {
	results: any[]; // You can replace `any` with a proper structure if you want
};

async function fetchDataAndSave() {
	try {
		const response = await axios.get<WgerExerciseResponse>(
			"https://wger.de/api/v2/exerciseinfo/?limit=500",
			{
				headers: {
					Accept: "application/json",
				},
			}
		);

		const rawExercises = response.data.results;

		const formattedExercises = rawExercises
    .filter((item: any) => item.images?.length > 0)
    .map((item: any) => {
			const englishTranslation = item.translations.find(
				(t: any) => t.language === 2
			);
			const equipmentName = item.equipment?.[0]?.name ?? null;

			return {
				id: item.id,
				bodyPart: item.category?.name ?? null,
				equipment: equipmentName?.toLowerCase().includes("bodyweight")
					? null
					: equipmentName,
				name: englishTranslation?.name ?? null,
				description: englishTranslation?.description,
				// ? cleanText(englishTranslation.description)
				// : null,
				imageURL: item.images?.[0]?.image ?? null,
				muscleGroup: item.muscles?.[0]?.name_en ?? null,
				isGeneric: true,
			};
		});

		const filePath = path.join(__dirname, "/output.json");
		fs.writeFileSync(filePath, JSON.stringify(formattedExercises, null, 2));

		console.log("✅ Final cleaned data saved to output.json");
	} catch (error: any) {
		console.error("❌ Error fetching data:", error.message);
	}
}

fetchDataAndSave();
