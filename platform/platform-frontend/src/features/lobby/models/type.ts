export const Type = {
    String: "STRING",
    Integer: "INTEGER",
    Boolean: "BOOLEAN",
    Enum: "ENUM",
} as const;

export type Type = (typeof Type)[keyof typeof Type];
