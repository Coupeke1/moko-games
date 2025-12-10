export const KingMovementMode = {
    SINGLE: "SINGLE",
    DOUBLE: "DOUBLE",
    FLYING: "FLYING"
} as const;

export type KingMovementMode = (typeof KingMovementMode)[keyof typeof KingMovementMode];

