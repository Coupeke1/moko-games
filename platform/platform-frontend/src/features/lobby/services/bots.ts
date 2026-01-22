import { environment } from "@/config.ts";
import { client } from "@/lib/api-client.ts";
import { validIdCheck } from "@/lib/id.ts";

const BASE_URL = environment.sessionService;

export async function addBot(lobby: string) {
    try {
        validIdCheck(lobby);
        await client.post(`${BASE_URL}/${lobby}/invite/bot`);
    } catch {
        throw new Error(`Could not add bot to lobby with id '${lobby}'`);
    }
}

export async function removeBot(lobby: string) {
    try {
        validIdCheck(lobby);
        await client.delete(`${BASE_URL}/${lobby}/bot`);
    } catch {
        throw new Error(`Could not add bot to lobby with id '${lobby}'`);
    }
}
