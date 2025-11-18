export const Justify = {
    None: "",
    Center: "justify-center",
    Between: "justify-between",
    Start: "justify-start",
    End: "justify-end",
} as const;

export type Justify = (typeof Justify)[keyof typeof Justify];
