import { environment } from "@/config.ts";
import type { Entry } from "@/features/cart/models/entry.ts";
import { client } from "@/lib/api-client.ts";

const BASE_URL = environment.cartService;

export async function findEntries(): Promise<Entry[]> {
    try {
        const { data } = await client.get<Entry[]>(BASE_URL);
        return data;
    } catch {
        throw new Error("Cart could not be fetched");
    }
}

export async function hasEntry(id: string): Promise<boolean> {
    try {
        const { data } = await client.get<boolean>(`${BASE_URL}/${id}`);
        return data;
    } catch {
        throw new Error("Could not check is entry is in cart");
    }
}

export function getTotalPrice(entries: Entry[]): number {
    return entries.reduce((total, entry) => total + entry.price, 0);
}

export async function addToCart(entry: Entry): Promise<void> {
    try {
        await client.post(BASE_URL, {
            id: entry.id,
        });
    } catch {
        throw new Error("Could not add entry to cart");
    }
}

export async function removeFromCart(entry: Entry): Promise<void> {
    try {
        await client.delete(`${BASE_URL}/${entry.id}`);
    } catch {
        throw new Error("Could not remove entry from cart");
    }
}
