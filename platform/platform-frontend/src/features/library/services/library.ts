import { environment } from "@/config.ts";
import { client } from "@/lib/api-client.ts";
import type { Entry } from "@/features/library/models/entry.ts";

const BASE_URL = environment.libraryService;

export async function findEntries(query?: string): Promise<Entry[]> {
    try {
        const params = new URLSearchParams();
        if (query) params.append("query", query);

        const url = params.toString()
            ? `${BASE_URL}/me?${params.toString()}`
            : `${BASE_URL}/me`;

        const { data } = await client.get<Entry[]>(url);
        return data;
    } catch {
        throw new Error("Library could not be fetched");
    }
}

export async function isEntryFavourited(id: string): Promise<boolean> {
    try {
        const { data } = await client.get<boolean>(
            `${BASE_URL}/${id}/favourite`,
        );

        return data;
    } catch {
        throw new Error("Could not check if entry is favourited");
    }
}

export async function hasEntry(id: string): Promise<boolean> {
    try {
        const { data } = await client.get<boolean>(`${BASE_URL}/${id}`);

        return data;
    } catch {
        throw new Error("Could not check is entry is in library");
    }
}

export async function favouriteEntry(id: string): Promise<void> {
    try {
        await client.patch(`${BASE_URL}/${id}/favourite`);
    } catch {
        throw new Error("Entry could not be favourited");
    }
}

export async function unFavouriteEntry(id: string): Promise<void> {
    try {
        await client.patch(`${BASE_URL}/${id}/unfavourite`);
    } catch {
        throw new Error("Entry could not be unfavourited");
    }
}
