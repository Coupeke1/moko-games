import { client } from "@/lib/api-client.ts";
import type { Game } from "@/features/games/models/game.ts";
import { environment } from "@/config.ts";

const BASE_URL = environment.gamesService;

export async function findGame(id: string): Promise<Game> {
    try {
        const { data } = await client.get<Game>(`${BASE_URL}/${id}`);
        return data;
    } catch {
        throw new Error("Game could not be fetched");
    }
}
