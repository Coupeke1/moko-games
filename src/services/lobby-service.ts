import { client } from "@/lib/api-client";
import { validIdCheck } from "@/lib/id";
import type { Game } from "@/models/library/game";
import type { Lobby } from "@/models/lobby/lobby";
import type { Player } from "@/models/lobby/player";

const BASE_URL = import.meta.env.VITE_SESSION_SERVICE;

export async function findLobby(id: string): Promise<Lobby> {
    try {
        const { data } = await client.get<Lobby>(`${BASE_URL}/${id}`);

        return data;
    } catch {
        throw new Error("Lobby could not be fetched");
    }
}

export async function createLobby(game: Game, size: number): Promise<Lobby> {
    try {
        const type = game.title === "Tic Tac Toe" ? "ticTacToe" : game.title === "Checkers" ? "checkers" : null;

        const settings = game.title === "Tic Tac Toe" ? {
            type,
            boardSize: 3
        } : game.title === "Checkers" ? {
            type,
            boardSize: 8,
            flyingKings: false
        } : null;

        const { data } = await client.post<Lobby>(BASE_URL, {
            gameId: game.id,
            maxPlayers: size,
            settings: settings
        });

        return data;
    } catch {
        throw new Error("Lobby could not be created");
    }
}


export function isPlayerInLobby(user: string, lobby: Lobby): boolean {
    try {
        validIdCheck(user);

        return lobby.players.find(
            (player: Player) => player.id === user
        ) !== undefined;
    }
    catch {
        throw new Error(`Cannot check if user with id '${user}' is in lobby with id '${lobby.id}'`);
    }
}

export function findOwner(lobby: Lobby): Player {
    try {
        const owner: Player | undefined = lobby.players.find(
            (player: Player) => player.id === lobby.ownerId
        );

        if (owner === undefined) throw new Error("Owner not found");
        return owner;
    }
    catch {
        throw new Error(`Cannot fetch owner of lobby with id '${lobby.id}'`);
    }
}

export async function sendInvite(user: string, lobby: string) {
    try {
        validIdCheck(user);
        validIdCheck(lobby);

        await client.post(`${BASE_URL}/${lobby}/invite/${user}`);
    } catch {
        throw new Error(`Invite to user with id '${user}' could not be sent`);
    }
}

export async function findInvites(game: string) {
    try {
        const { data } = await client.get<Lobby[]>(`${BASE_URL}/invited/${game}/me`);
        return data;
    } catch {
        throw new Error(`Invites to user could not be fetched`);
    }
}

export async function acceptInvite(lobby: string) {
    try {
        validIdCheck(lobby);
        await client.post(`${BASE_URL}/${lobby}/invite/accept/me`);
    } catch {
        throw new Error(`Invite could not be accepted`);
    }
}