import { environment } from "@/config";
import { client } from "@/lib/api-client";
import type { Entry } from "@/models/library/entry";

const BASE_URL = environment.libraryService;

export async function findEntries(): Promise<Entry[]> {
    try {
        const { data } = await client.get<Entry[]>(`${BASE_URL}/me`);
        return data;
    } catch {
        throw new Error("Library could not be fetched");
    }
}

export async function isEntryFavourited(id: string) {
    try {
        const { data } = await client.get<boolean>(
            `${BASE_URL}/${id}/favourite`,
        );

        return data;
    } catch {
        throw new Error(
            `Could not check if Entry with id '${id}' is favourited`,
        );
    }
}

export async function favouriteEntry(id: string) {
    try {
        await client.patch(`${BASE_URL}/${id}/favourite`);
    } catch {
        throw new Error(`Entry with id '${id}' could not be favourited`);
    }
}

export async function unFavouriteEntry(id: string) {
    try {
        await client.patch(`${BASE_URL}/${id}/unfavourite`);
    } catch {
        throw new Error(`Entry with id '${id}' could not be unfavourited`);
    }
}
