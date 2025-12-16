import {environment} from "@/config.ts";
import {client} from "@/lib/api-client.ts";
import type {GameSettingsSchema} from "@/features/lobby/models/settings.ts";

const GAMES_URL = environment.gamesService;

export async function findGameSettingsSchema(gameId: string): Promise<GameSettingsSchema> {
    try {
        const {data} = await client.get<GameSettingsSchema>(`${GAMES_URL}/${gameId}/settings`);
        return data;
    } catch {
        const {data} = await client.get<{ settings: GameSettingsSchema["settings"] }>(`${GAMES_URL}/${gameId}`);
        return {settings: data.settings ?? []};
    }
}