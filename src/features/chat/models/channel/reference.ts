import type { Type } from "@/features/chat/models/channel/type";
import type { User } from "@/features/chat/models/channel/user";

export interface BotReference {
    type: typeof Type.Bot;
    userId: string;
}

export interface LobbyReference {
    type: typeof Type.Lobby;
    lobbyId: string;
}

export interface PrivateReference {
    type: typeof Type.Friends;
    user: User;
    otherUser: User;
}
