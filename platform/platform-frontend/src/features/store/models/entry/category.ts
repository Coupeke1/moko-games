export const Category = {
    Strategy: "STRATEGY",
    Family: "FAMILY",
    Card: "CARD",
    Party: "PARTY",
    Abstract: "ABSTRACT",
    Other: "OTHER",
} as const;

export type Category = (typeof Category)[keyof typeof Category];

export function format(category: Category | string) {
    return `${category.charAt(0)}${category.slice(1).toLowerCase()}`;
}
