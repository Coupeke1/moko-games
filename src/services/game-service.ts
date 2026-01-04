import validIdCheck from "@/lib/id.ts";
import type {GameState} from "@/models/game-state";
import axios from "axios";
import type {MoveRequest} from "@/models/move-request";
import {config} from "@/config.ts";

const BASE_URL = config.ticTacToeService;

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

export async function requestMove(gameId: string, playerId: string, rowIndex: number, colIndex: number) {
    try {
        const moveRequest: MoveRequest = {
            gameId: gameId,
            playerId: playerId,
            row: rowIndex,
            col: colIndex,
        };

        const response = await axios.post<GameState>(`${BASE_URL}/${gameId}/move`, moveRequest);
        return response.data;
    } catch (error) {
        if (error instanceof Error) {
            throw new Error(`Could not make move: ${error.message}`);
        }
        throw new Error(`Could make move: Unknown error`);
    }
}

export async function requestBotMove(gameId: string): Promise<GameState> {
    try {
        const response = await axios.post<GameState>(`${BASE_URL}/${gameId}/move-bot`);
        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            throw new Error(`Could not request bot move: ${error.response?.data?.message || error.message}`);
        }
        throw new Error(`Could not request bot move: Unknown error`);
    }
}