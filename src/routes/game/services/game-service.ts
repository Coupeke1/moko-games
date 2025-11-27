import validIdCheck from "@/lib/id.ts";
import type {GameState} from "@/routes/game/model/game-state.ts";
import axios from "axios";
import type {MoveRequest} from "@/routes/game/model/move-request.ts";
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

export async function resetGame(gameId: string) {
    try {
        const response = await axios.post<GameState>(`${BASE_URL}/${gameId}/reset`);
        return response.data;
    } catch (error) {
        if (error instanceof Error) {
            throw new Error(`Could not reset game: ${error.message}`);
        }
        throw new Error(`Could reset game: Unknown error`);
    }
}