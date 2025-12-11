import {client} from "@/lib/api-client";
import {environment} from "@/config";
import type {AchievementListModel} from "@/features/profile/models/AchievementListModel";

const BASE_URL = environment.userServiceAchievements;

export async function findMyAchievements(): Promise<AchievementListModel> {
    console.log("Fetching achievements");
    console.log(BASE_URL);
    try {
        const {data} = await client.get<AchievementListModel>(
            `${BASE_URL}/me`
        );
        console.log(data);
        return data;
    } catch (err) {
        console.log(err);
        throw new Error("Failed to fetch achievements" + err);
    }
}