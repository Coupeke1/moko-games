import validIdCheck from "@/lib/id.ts";
import type {GameState} from "@/routes/game/model/game-state.ts";
import axios from "axios";

const BASE_URL = import.meta.env.VITE_TICTACTOE_API;

export async function getGameState(gameId: string) : Promise<GameState> {
    try {
        validIdCheck(gameId);

        const { data } = await axios.get<GameState>(`${BASE_URL}/${gameId}`);
        return data
    } catch (error) {
        if (error instanceof Error) {
            throw new Error(`Could not fetch game with id '${gameId}': ${error.message}`);
        }
        throw new Error(`Could not fetch game with id '${gameId}': Unknown error`);
    }
}