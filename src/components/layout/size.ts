export const Height = {
    None: "",
    Small: "min-h-24",
    Medium: "min-h-32",
    Large: "min-h-48",
    ExtraLarge: "min-h-56",
    Gigantic: "min-h-64",
} as const;

export type Height = (typeof Height)[keyof typeof Height];
