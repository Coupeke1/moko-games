export const Items = {
    None: "",
    Stretch: "items-stretch",
    Center: "items-center",
    Between: "items-between",
    Start: "items-start",
    End: "items-end",
} as const;

export type Items = (typeof Items)[keyof typeof Items];
