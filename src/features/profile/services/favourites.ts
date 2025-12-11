import {client} from "@/lib/api-client";
import {environment} from "@/config";
import type {LibraryItem} from "@/features/profile/models/Library";

const BASE_URL = environment.libraryService;

interface LibraryParams {
    query?: string;
    favourite?: boolean;
}

export async function findMyLibrary(params?: LibraryParams): Promise<LibraryItem[]> {
    try {
        const queryParams = new URLSearchParams();

        if (params?.query) {
            queryParams.append('query', params.query);
        }

        if (params?.favourite !== undefined) {
            queryParams.append('favourite', params.favourite.toString());
        }

        const url = queryParams.toString()
            ? `${BASE_URL}/me?${queryParams.toString()}`
            : `${BASE_URL}/me`;

        const {data} = await client.get<LibraryItem[]>(url);
        return data;
    } catch (err) {
        throw new Error("Failed to fetch library: " + err);
    }
}