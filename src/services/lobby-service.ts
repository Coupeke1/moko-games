import { client } from "@/lib/api-client";
import type { Game } from "@/models/library/game";
import type { Lobby } from "@/models/lobby/lobby";

const BASE_URL = import.meta.env.VITE_SESSION_SERVICE;

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