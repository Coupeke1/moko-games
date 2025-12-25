export interface Notifications {
    receiveEmail: boolean;
    social: boolean;
    achievements: boolean;
    commerce: boolean;
    chat: boolean;
}

export function match(a: Notifications, b: Notifications): boolean {
    return (
        a.receiveEmail === b.receiveEmail &&
        a.social === b.social &&
        a.achievements === b.achievements &&
        a.commerce === b.commerce &&
        a.chat === b.chat
    );
}
