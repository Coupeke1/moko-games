import { environment } from "@/config";
import type { Achievement } from "@/features/profiles/models/achievement";
import { client } from "@/lib/api-client";

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

export async function findMyAchievementsForGame(
    id: string,
): Promise<Achievement[]> {
    try {
        const { data } = await client.get<{ achievements: Achievement[] }>(
            `${BASE_URL}/me/${id}`,
        );
        return data.achievements;
    } catch {
        throw new Error("Could not fetch achievements");
    }
}
