export const GameStatus = {
    IN_PROGRESS: "IN_PROGRESS", WON: "WON",TIE: "TIE"
} as const;

export type GameStatus = (typeof GameStatus)[keyof typeof GameStatus];