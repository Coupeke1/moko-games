export const Padding = {
    None: "p-0",
    Small: "p-1",
    Medium: "p-2",
    Large: "p-4",
    ExtraLarge: "p-6",
    Gigantic: "p-12"
} as const;

export type Padding = (typeof Padding)[keyof typeof Padding];
