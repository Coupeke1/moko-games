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

export async function isGameFavourited(id: string) {
    try {
        const { data } = await client.get<boolean>(
            `${BASE_URL}/${id}/favourite`,
        );

        return data;
    } catch {
        throw new Error(
            `Could not check if game with id '${id}' is favourited`,
        );
    }
}

export async function favouriteGame(id: string) {
    try {
        console.log("fav");
        await client.patch(`${BASE_URL}/${id}/favourite`);
    } catch {
        throw new Error(`Game with id '${id}' could not be favourited`);
    }
}

export async function unFavouriteGame(id: string) {
    try {
        console.log("un");
        await client.patch(`${BASE_URL}/${id}/unfavourite`);
    } catch {
        throw new Error(`Game with id '${id}' could not be unfavourited`);
    }
}
