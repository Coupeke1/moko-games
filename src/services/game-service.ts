import { client } from "@/lib/api-client";
import type { Game } from "@/models/game/game";
import {config} from "@/config.ts";

const BASE_URL = config.gamesService;

export async function findGame(id: string): Promise<Game> {
    try {
        const { data } = await client.get<Game>(`${BASE_URL}/${id}`);
        return data;
    } catch {
        throw new Error("Game could not be fetched");
    }
}