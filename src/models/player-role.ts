export const PlayerRole = {
    BLACK: "BLACK",
    WHITE: "WHITE"
} as const;

export type PlayerRole = (typeof PlayerRole)[keyof typeof PlayerRole];