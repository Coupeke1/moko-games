import { client } from "@/lib/api-client";
import { validIdCheck } from "@/lib/id";
import type { Game } from "@/models/library/game";
import type { Lobby } from "@/models/lobby/lobby";
import type { Player } from "@/models/lobby/player";
import {config} from "@/config.ts";

const BASE_URL = config.sessionService;

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
        const { data } = await client.post<Lobby>(BASE_URL, {
            gameId: game.id,
            maxPlayers: size,
            settings: getGameSettings(game.title),
        });

        return data;
    } catch {
        throw new Error("Lobby could not be created");
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
        throw new Error(
            `Cannot check if user with id '${user}' is in lobby with id '${lobby.id}'`,
        );
    }
}

export function isUserOwner(user: string, lobby: Lobby): boolean {
    try {
        const owner: Player = findOwner(lobby);
        return user === owner.id;
    } catch {
        throw new Error(
            `Cannot check if user with id '${user}' is owner of lobby with id '${lobby.id}'`,
        );
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
        const { data } = await client.get<Lobby[]>(
            `${BASE_URL}/invited/${game}/me`,
        );
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

export async function removePlayer(player: string, lobby: string) {
    try {
        validIdCheck(player);
        validIdCheck(lobby);
        await client.delete(`${BASE_URL}/${lobby}/players/${player}`);
    } catch {
        throw new Error(
            `Player with id '${player}' could not be removed from lobby with id '${lobby}'`,
        );
    }
}

export async function readyPlayer(lobby: string) {
    try {
        validIdCheck(lobby);
        await client.patch(`${BASE_URL}/${lobby}/players/ready`);
    } catch {
        throw new Error(`Player could not ready up`);
    }
}

export async function unReadyPlayer(lobby: string) {
    try {
        validIdCheck(lobby);
        await client.patch(`${BASE_URL}/${lobby}/players/unready`);
    } catch {
        throw new Error(`Player could not cancel ready up`);
    }
}

export function allPlayersReady(lobby: Lobby): boolean {
    try {
        return (
            lobby.players.find((player: Player) => !player.ready) === undefined
        );
    } catch {
        throw new Error(
            `Cannot check if all players in lobby with id '${lobby.id}' are ready`,
        );
    }
}

export async function closeLobby(lobby: string) {
    try {
        validIdCheck(lobby);
        await client.post(`${BASE_URL}/${lobby}/close`);
    } catch {
        throw new Error(`Could not close lobby with id '${lobby}'`);
    }
}

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

export async function updateSettings(
    lobby: string,
    title: string,
    size: number,
) {
    try {
        validIdCheck(lobby);

        await client.put(`${BASE_URL}/${lobby}/settings`, {
            maxPlayers: size,
            settings: getGameSettings(title),
        });
    } catch {
        throw new Error(
            `Could not update settings for lobby with id '${lobby}'`,
        );
    }
}

/* TODO: remove this to work with `game-service` */
function getGameSettings(title: string) {
    return title === "Tic Tac Toe"
        ? {
              type: "ticTacToe",
              boardSize: 3,
          }
        : title === "Checkers"
          ? {
                type: "checkers",
                boardSize: 8,
                flyingKings: false,
            }
          : null;
}
