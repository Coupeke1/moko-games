import type {Player} from "./player";
import {PlayerRole} from "./player-role";
import {KingMovementMode} from "./king-movement-mode";
import {GameStatus} from "./game-status";


export interface GameState {
    id: string;
    players: Player[];
    botPlayer: PlayerRole | null;
    currentRole: PlayerRole;
    board: string[][];
    kingMovementMode: KingMovementMode;
    status: GameStatus;
}