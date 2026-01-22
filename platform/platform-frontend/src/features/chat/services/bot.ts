import { environment } from "@/config.ts";
import type { Bot } from "@/features/profiles/models/bot";
import { client } from "@/lib/api-client.ts";

const BASE_URL = environment.userService;

export async function createBot(id: string, game: string): Promise<Bot> {
    try {
        const { data } = await client.post<Bot>(`${BASE_URL}/bot/${id}`, game, {
            headers: { "Content-Type": "text/plain" },
        });
        return data;
    } catch {
        throw new Error("Bot could not be created");
    }
}
