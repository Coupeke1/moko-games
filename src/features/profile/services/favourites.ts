import { client } from "@/lib/api-client";
import { environment } from "@/config";
import type { Entry } from "@/features/library/models/entry";

const BASE_URL = environment.libraryService;

interface LibraryParams {
    query?: string;
    favourite?: boolean;
}

export async function findMyLibrary(params?: LibraryParams): Promise<Entry[]> {
    try {
        const queryParams = new URLSearchParams();

        if (params?.query) {
            queryParams.append("query", params.query);
        }

        if (params?.favourite !== undefined) {
            queryParams.append("favourite", params.favourite.toString());
        }

        const url = queryParams.toString()
            ? `${BASE_URL}/me?${queryParams.toString()}`
            : `${BASE_URL}/me`;

        const { data } = await client.get<Entry[]>(url);
        return data;
    } catch {
        throw new Error("Could not fetch favourites");
    }
}
