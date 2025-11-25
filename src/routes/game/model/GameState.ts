import type {Player} from "@/routes/game/model/Player.ts";
import type {GameStatus} from "@/routes/game/model/GameStatus.ts";

export interface GameState {
    id: string;
    board: string[][];
    status: GameStatus;
    players: Player[];
    currentRole: string;
    winner?: string;
}
