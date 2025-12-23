export interface Modules {
    achievements: boolean;
    favourites: boolean;
}

export function match(a: Modules, b: Modules): boolean {
    return a.achievements === b.achievements && a.favourites === b.favourites;
}
