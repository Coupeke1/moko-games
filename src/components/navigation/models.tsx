import type { ReactNode } from "react";

import LibraryIcon from "@/components/icons/bar/library-icon";
import ProfileIcon from "@/components/icons/bar/profile-icon";
import StoreIcon from "@/components/icons/bar/store-icon";
import UsersIcon from "@/components/icons/users-icon";
import NotificationsIcon from "@/components/icons/bar/notifications-icon";
import ChatIcon from "@/components/icons/bar/chat-icon";
import CartIcon from "@/components/icons/bar/cart-icon";

export interface Link {
    title: string;
    icon: ReactNode;
    path: string;
}

export interface Button {
    title: string;
    icon: ReactNode;
    onClick: () => void;
}

export function getLinks(): Link[] {
    return [
        { title: "Store", icon: <StoreIcon />, path: "/store" },
        { title: "Library", icon: <LibraryIcon />, path: "/library" },
        { title: "Profile", icon: <ProfileIcon />, path: "/profile" },
        { title: "Friends", icon: <UsersIcon />, path: "/friends" },
    ];
}

export function getButtons(): Button[] {
    return [
        {
            title: "Notifications",
            icon: <NotificationsIcon />,
            onClick: () => {},
        },
        { title: "Chat", icon: <ChatIcon />, onClick: () => {} },
        { title: "Cart", icon: <CartIcon />, onClick: () => {} },
    ];
}
