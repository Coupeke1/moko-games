import type { PlayerRole } from "@/routes/game/model/player-role.ts";

export interface Player {
    id: string;
    role: PlayerRole;
}