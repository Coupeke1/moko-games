import { environment } from "@/config";
import { client } from "@/lib/api-client";
import type { Entries } from "@/models/store/entries";

const BASE_URL = environment.storeService;

export async function findEntries(): Promise<Entries> {
    try {
        const { data } = await client.get<Entries>(BASE_URL);
        return data;
    } catch {
        throw new Error("Catalog could not be fetched");
    }
}
