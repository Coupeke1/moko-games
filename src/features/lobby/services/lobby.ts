import {environment} from "@/config.ts";
import type {Game} from "@/features/games/models/game.ts";
import type {Lobby} from "@/features/lobby/models/lobby.ts";
import type {Player} from "@/features/lobby/models/player.ts";
import {Status} from "@/features/lobby/models/status";
import {client} from "@/lib/api-client.ts";
import {validIdCheck} from "@/lib/id.ts";

const BASE_URL = environment.sessionService;

export async function findLobby(id: string): Promise<Lobby> {
    try {
        const {data} = await client.get<Lobby>(`${BASE_URL}/${id}`);
        return data;
    } catch {
        throw new Error("Lobby could not be fetched");
    }
}

export async function findLobbyGameSettings(lobbyId: string): Promise<Record<string, unknown>> {
    try {
        validIdCheck(lobbyId);
        const {data} = await client.get<Record<string, unknown>>(
            `${BASE_URL}/${lobbyId}/settings`,
        );
        return data ?? {};
    } catch {
        throw new Error("Could not fetch lobby game settings");
    }
}

export async function createLobby(game: Game, size: number): Promise<Lobby> {
    try {
        const {data} = await client.post<Lobby>(BASE_URL, {
            gameId: game.id,
            maxPlayers: size,
        });

        return data;
    } catch {
        throw new Error("Lobby could not be created");
    }
}

export function isUserOwner(user: string, lobby: Lobby): boolean {
    try {
        const owner: Player = findOwner(lobby);
        return user === owner.id;
    } catch {
        throw new Error("Could not check if user is owner");
    }
}

export function findOwner(lobby: Lobby): Player {
    try {
        const owner: Player | undefined = lobby.players.find(
            (player: Player) => player.id === lobby.ownerId,
        );

        if (owner === undefined) throw new Error("Owner not found");
        return owner;
    } catch {
        throw new Error("Could not fetch owner of lobby");
    }
}

export function shouldStart(lobby: Lobby): boolean {
    return lobby.status === Status.Started;
}

export function isClosed(lobby: Lobby): boolean {
    return lobby.status === Status.Closed || lobby.status === Status.Finished;
}

export function allPlayersReady(lobby: Lobby): boolean {
    return lobby.players.find((player: Player) => !player.ready) === undefined;
}

export async function closeLobby(lobby: string) {
    try {
        validIdCheck(lobby);
        await client.post(`${BASE_URL}/${lobby}/close`);
    } catch {
        throw new Error("Could not close lobby");
    }
}

export async function updateSettings(
    lobby: string,
    size: number,
    settings?: Record<string, unknown>,
) {
    try {
        validIdCheck(lobby);

        await client.put(`${BASE_URL}/${lobby}/settings`, {
            maxPlayers: size,
            ...(settings ? {settings} : {}),
        });
    } catch {
        throw new Error("Could not update settings");
    }
}

export async function startGame(lobby: string): Promise<string> {
    try {
        validIdCheck(lobby);
        const {data} = await client.post(`${BASE_URL}/${lobby}/start`);
        return data;
    } catch {
        throw new Error("Could not start game");
    }
}