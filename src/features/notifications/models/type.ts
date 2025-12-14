export const Type = {
    Unread: "UNREAD",
    Read: "READ",
};

export type Type = (typeof Type)[keyof typeof Type];
