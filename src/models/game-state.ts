import type { GameStatus } from "@/routes/game/model/game-status.ts";
import type { Player } from "@/routes/game/model/Player.ts";
import type { PlayerRole } from "@/routes/game/model/player-role.ts";
import type { KingMovementMode } from "@/routes/game/model/king-movement-mode.ts";

export interface GameState {
    id: string;
    players: Player[];
    aiPlayer: PlayerRole | null;
    currentRole: PlayerRole;
    board: string[][];
    kingMovementMode: KingMovementMode;
    status: GameStatus;
}