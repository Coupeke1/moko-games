import validIdCheck from "@/lib/id.ts";
import axios from "axios";
import {type GameState} from "../models/game-state";

const BASE_URL = import.meta.env.VITE_CHECKERS_API;

export async function getGameState(gameId: string): Promise<GameState> {
    try {
        validIdCheck(gameId);

        const {data} = await axios.get<GameState>(`${BASE_URL}/${gameId}`);
        return data
    } catch (error) {
        if (error instanceof Error) {
            throw new Error(`Could not fetch game with id '${gameId}': ${error.message}`);
        }
        throw new Error(`Could not fetch game with id '${gameId}': Unknown error`);
    }
}

export async function requestMove(gameId: string, playerId: string, cells: number[]) {
    try {
        const moveRequest = {
            playerId: playerId,
            cells: cells,
        };

        const response = await axios.post<GameState>(`${BASE_URL}/${gameId}/move`, moveRequest);
        return response.data;
    } catch (error) {
        if (error instanceof Error) {
            throw new Error(`Could not make move: ${error.message}`);
        }
        throw new Error(`Could not make move: Unknown error`);
    }
}

export async function resetGame(gameId: string) {
    try {
        const response = await axios.post<GameState>(`${BASE_URL}/${gameId}/reset`);
        return response.data;
    } catch (error) {
        if (error instanceof Error) {
            throw new Error(`Could not reset game: ${error.message}`);
        }
        throw new Error(`Could not reset game: Unknown error`);
    }
}