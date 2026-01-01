import type { Player } from "@/features/lobby/models/player.ts";
import type { Status } from "@/features/lobby/models/status.ts";
import type { Bot } from "@/features/profiles/models/bot.ts";

export const Reason = {
    Kicked: "kicked",
} as const;

export type Reason = (typeof Reason)[keyof typeof Reason];

export interface LobbyMessage {
    payload: Lobby | null;
    reason?: Reason;
}

export interface Lobby {
    id: string;
    gameId: string;
    startedGameId: string | null;
    ownerId: string;
    players: Player[];
    bot: Bot;
    maxPlayers: number;
    status: Status;
    settings: Record<string, unknown>;
}
