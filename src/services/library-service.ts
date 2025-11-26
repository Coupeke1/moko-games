import { client } from "@/lib/api-client";
import type { Game } from "@/models/library/game";

const BASE_URL = import.meta.env.VITE_LIBRARY_SERVICE;

export async function findGames(): Promise<Game[]> {
    try {
        const { data } = await client.get<Game[]>(`${BASE_URL}/me`);
        return data;
    } catch {
        throw new Error("Library could not be fetched");
    }
}