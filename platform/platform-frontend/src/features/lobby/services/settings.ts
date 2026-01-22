import { environment } from "@/config.ts";
import { client } from "@/lib/api-client.ts";
import type { Schema } from "@/features/lobby/models/settings.ts";

const BASE_URL = environment.gamesService;

export async function findSchema(id: string): Promise<Schema> {
    try {
        const { data } = await client.get<Schema>(`${BASE_URL}/${id}/settings`);
        return data;
    } catch {
        const { data } = await client.get<{ settings: Schema["settings"] }>(
            `${BASE_URL}/${id}`,
        );
        return { settings: data.settings ?? [] };
    }
}
