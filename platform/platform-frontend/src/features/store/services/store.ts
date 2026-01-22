import { environment } from "@/config.ts";
import { client } from "@/lib/api-client.ts";
import type { Entry } from "@/features/store/models/entry/entry.ts";
import type { PagedResponse } from "@/types/paged-response";

const BASE_URL = environment.storeService;

export async function findEntries(
    query?: string,
    sorting?: string,
    category?: string,
): Promise<PagedResponse<Entry>> {
    try {
        const params = new URLSearchParams();

        if (query) params.append("query", query);
        if (sorting) params.append("sorting", sorting);
        if (category && category !== "all")
            params.append("category", category.toUpperCase());

        const url = params.toString()
            ? `${BASE_URL}?${params.toString()}`
            : BASE_URL;

        const { data } = await client.get<PagedResponse<Entry>>(url);
        return data;
    } catch {
        throw new Error("Catalog could not be fetched");
    }
}

export async function findEntry(id: string): Promise<Entry> {
    try {
        const { data } = await client.get<Entry>(`${BASE_URL}/${id}`);
        return data;
    } catch {
        throw new Error("Entry could not be fetched");
    }
}
