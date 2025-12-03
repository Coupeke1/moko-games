import { environment } from "@/config";
import { client } from "@/lib/api-client";
import type { Game } from "@/models/library/game";

const BASE_URL = environment.libraryService;

export async function findGames(): Promise<Game[]> {
    try {
        const { data } = await client.get<Game[]>(`${BASE_URL}/me`);
        return data;
    } catch {
        throw new Error("Library could not be fetched");
    }
}

export async function favouriteGame(id: string) {
    try {
        // TODO
    } catch {
        throw new Error(`Game with id '${id}' could not be favourited`);
    }
}

export async function unFavouriteGame(id: string) {
    try {
        // TODO
    } catch {
        throw new Error(`Game with id '${id}' could not be unfavourited`);
    }
}
