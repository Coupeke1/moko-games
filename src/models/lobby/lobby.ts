import type { Player } from "@/models/lobby/player";
import type { Status } from "@/models/lobby/status";
import type { Bot } from "@/models/profile/bot";

export interface Lobby {
    id: string;
    gameId: string;
    startedGameId: string;
    ownerId: string;
    players: Player[];
    bot: Bot;
    maxPlayers: number;
    status: Status;
}
