import { environment } from "@/config";
import { client } from "@/lib/api-client";
import type { Entries } from "@/models/store/entries";

const BASE_URL = environment.storeService;

export async function findEntries(
    query?: string,
    sorting?: string,
    category?: string,
): Promise<Entries> {
    try {
        const params = new URLSearchParams();

        if (query) params.append("query", query);
        if (sorting) params.append("sorting", sorting);
        if (category && category !== "all")
            params.append("category", category.toUpperCase());

        const url = params.toString()
            ? `${BASE_URL}?${params.toString()}`
            : BASE_URL;

        const { data } = await client.get<Entries>(url);
        return data;
    } catch {
        throw new Error("Catalog could not be fetched");
    }
}
