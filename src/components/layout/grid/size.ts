export const GridSize = {
    Small: "md:grid-cols-2",
    Medium: "md:grid-cols-2 lg:grid-cols-3",
    Large: "sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4",
} as const;

export type GridSize = (typeof GridSize)[keyof typeof GridSize];
