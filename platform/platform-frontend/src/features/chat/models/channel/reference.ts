import type { Type } from "@/features/chat/models/channel/type";
import type { User } from "@/features/chat/models/channel/user";
import type { Bot } from "@/features/profiles/models/bot";

export interface BotReference {
    type: typeof Type.Bot;
    user: User;
    bot: Bot;
    gameId: string;
}

export interface LobbyReference {
    type: typeof Type.Lobby;
    lobbyId: string;
    players: User[];
}

export interface PrivateReference {
    type: typeof Type.Friends;
    user: User;
    otherUser: User;
}
