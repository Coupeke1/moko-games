import type { Player } from "@/features/lobby/models/player.ts";
import type { Status } from "@/features/lobby/models/status.ts";
import type { Bot } from "@/features/profile/models/bot.ts";

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
