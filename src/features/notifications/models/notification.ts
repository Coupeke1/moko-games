export type NotificationType =
    | "FRIEND_REQUEST_RECEIVED"
    | "FRIEND_REQUEST_ACCEPTED"
    | "LOBBY_INVITE"
    | "PLAYER_JOINED_LOBBY"
    | "ACHIEVEMENT_UNLOCKED"
    | "ORDER_COMPLETED"
    | "DIRECT_MESSAGE";

export interface NotificationItem {
    id: string;
    type: NotificationType;
    title: string;
    message: string;
    createdAt: string;
    read: boolean;
}

export type ReadFilter = "all" | "unread" | "read";

export const NOTIFICATION_TYPE_LABEL: Record<NotificationType, string> = {
    FRIEND_REQUEST_RECEIVED: "Friend request received",
    FRIEND_REQUEST_ACCEPTED: "Friend request accepted",
    LOBBY_INVITE: "Lobby invite",
    PLAYER_JOINED_LOBBY: "Player joined lobby",
    ACHIEVEMENT_UNLOCKED: "Achievement unlocked",
    ORDER_COMPLETED: "Order completed",
    DIRECT_MESSAGE: "Direct message",
};