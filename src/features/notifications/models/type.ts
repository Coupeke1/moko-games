export const Origin = {
    FriendReceived: "FRIEND_REQUEST_RECEIVED",
    FriendAccepted: "FRIEND_REQUEST_ACCEPTED",
    LobbyInvite: "LOBBY_INVITE",
    LobbyJoined: "PLAYER_JOINED_LOBBY",
    Achievement: "ACHIEVEMENT_UNLOCKED",
    Order: "ORDER_COMPLETED",
    Message: "DIRECT_MESSAGE",
};

export type Origin = (typeof Origin)[keyof typeof Origin];

export function format(type: Origin | string) {
    return `${type.charAt(0)}${type.slice(1).toLowerCase().replaceAll("_", " ")}`;
}
