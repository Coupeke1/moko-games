import { environment } from "@/config.ts";
import type { Lobby } from "@/features/lobby/models/lobby.ts";
import type { Player } from "@/features/lobby/models/player.ts";
import { client } from "@/lib/api-client.ts";
import { validIdCheck } from "@/lib/id.ts";

const BASE_URL = environment.sessionService;

export async function removePlayer(player: string, lobby: string) {
    try {
        validIdCheck(player);
        validIdCheck(lobby);
        await client.delete(`${BASE_URL}/${lobby}/players/${player}`);
    } catch {
        throw new Error("Could not remove player from lobby");
    }
}

export async function readyPlayer(lobby: string) {
    try {
        validIdCheck(lobby);
        await client.patch(`${BASE_URL}/${lobby}/players/ready`);
    } catch {
        throw new Error("Could not ready up");
    }
}

export async function unReadyPlayer(lobby: string) {
    try {
        validIdCheck(lobby);
        await client.patch(`${BASE_URL}/${lobby}/players/unready`);
    } catch {
        throw new Error("Could not cancel ready up");
    }
}

export function isPlayerInLobby(user: string, lobby: Lobby): boolean {
    try {
        validIdCheck(user);

        if (!user) return true;

        return (
            lobby.players.find((player: Player) => player.id === user) !==
            undefined
        );
    } catch {
        throw new Error("Could not check if player is in lobby");
    }
}
