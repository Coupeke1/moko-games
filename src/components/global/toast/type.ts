export const Type = {
    Store: "Store",
    Checkout: "Checkout",
    Order: "Order",
    Cart: "Cart",
    Library: "Library",
    Profile: "Profile",
    Friends: "Friends",
    Lobby: "Lobby",
    Notifications: "Notifications",
    Chat: "Chat",
} as const;

export type Type = (typeof Type)[keyof typeof Type];
