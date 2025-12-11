import {client} from "@/lib/api-client";
import {environment} from "@/config";
import type {Achievement} from "@/features/profile/models/Achievement";

const BASE_URL = environment.achievementService;

export async function findMyAchievements(): Promise<Achievement[]> {
    try {
        const {data} = await client.get<Achievement[]>(
            `${BASE_URL}/me`
        );
        return data;
    } catch (err) {
        throw new Error("Failed to fetch achievements" + err);
    }
}