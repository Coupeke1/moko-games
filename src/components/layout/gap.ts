export const Gap = {
    None: "gap-0",
    Small: "gap-1",
    Medium: "gap-2",
    Large: "gap-4",
    ExtraLarge: "gap-6",
    Gigantic: "gap-12"
} as const;

export type Gap = (typeof Gap)[keyof typeof Gap];
