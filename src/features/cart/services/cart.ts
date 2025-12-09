import { environment } from "@/config.ts";
import { client } from "@/lib/api-client.ts";
import type { Entry } from "@/features/store/models/entry/entry.ts";

const BASE_URL = environment.cartService;

export async function findEntries(): Promise<Entry[]> {
    try {
        const { data } = await client.get<Entry[]>(BASE_URL);
        return data;
    } catch {
        throw new Error("Cart could not be fetched");
    }
}

export function getTotalPrice(entries: Entry[]): number {
    return entries.reduce((total, entry) => total + entry.price, 0);
}
