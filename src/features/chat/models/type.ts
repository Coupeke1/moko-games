export const Type = {
    Bot: "BOT",
    Lobby: "LOBBY",
    Friends: "FRIENDS",
} as const;

export type Type = (typeof Type)[keyof typeof Type];
