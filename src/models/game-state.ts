import type {GameStatus} from "@/models/game-status";
import type { Player } from "./player";

export interface GameState {
    id: string;
    board: string[][];
    status: GameStatus;
    players: Player[];
    currentRole: string;
    botPlayer: string;
    winner?: string;
}
