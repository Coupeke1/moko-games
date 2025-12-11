import { client } from "@/lib/api-client";
import { environment } from "@/config";
import type { Achievement } from "@/features/profile/models/achievement";

const BASE_URL = environment.achievementService;

export async function findMyAchievements(): Promise<Achievement[]> {
    try {
        const { data } = await client.get<{ achievements: Achievement[] }>(
            `${BASE_URL}/me`,
        );
        return data.achievements;
    } catch {
        throw new Error("Could not fetch achievements");
    }
}
