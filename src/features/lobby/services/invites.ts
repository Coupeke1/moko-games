import { environment } from "@/config.ts";
import type { Lobby } from "@/features/lobby/models/lobby.ts";
import { client } from "@/lib/api-client.ts";
import { validIdCheck } from "@/lib/id.ts";

const BASE_URL = environment.sessionService;

export async function sendInvite(user: string, lobby: string) {
    try {
        validIdCheck(user);
        validIdCheck(lobby);

        await client.post(`${BASE_URL}/${lobby}/invite/${user}`);
    } catch {
        throw new Error("Could not send invite");
    }
}

export async function findInvites(game: string) {
    try {
        const { data } = await client.get<Lobby[]>(
            `${BASE_URL}/invited/${game}/me`,
        );
        return data;
    } catch {
        throw new Error("Invites could not be fetched");
    }
}

export async function acceptInvite(lobby: string) {
    try {
        validIdCheck(lobby);
        await client.post(`${BASE_URL}/${lobby}/invite/accept/me`);
    } catch {
        throw new Error("Could not accept invite");
    }
}
