export const Category = {
    Strategy: "STRATEGY",
    Family: "FAMILY",
    Card: "CARD",
    Party: "PARTY",
    Abstract: "ABSTRACT",
    Other: "OTHER",
} as const;

export type Category = (typeof Category)[keyof typeof Category];
