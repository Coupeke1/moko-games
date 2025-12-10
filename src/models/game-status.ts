export const GameStatus = {
    RUNNING: "RUNNING",
    WHITE_WIN: "WHITE_WIN",
    BLACK_WIN: "BLACK_WIN",
    DRAW: "DRAW"
} as const;

export type GameStatus = (typeof GameStatus)[keyof typeof GameStatus];