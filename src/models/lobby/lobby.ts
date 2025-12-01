import type { Player } from "@/models/lobby/player";
import type { Status } from "@/models/lobby/status";

export interface Lobby {
    id: string;
    gameId: string;
    startedGameId: string;
    ownerId: string;
    players: Player[];
    maxPlayers: number;
    status: Status;
}