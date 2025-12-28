declare global {
    interface Window {
        __APP_CONFIG__?: {
            authUrl?: string;
            authRealm?: string;
            authClientId?: string;
            userService?: string;
            achievementService?: string;
            libraryService?: string;
            socialService?: string;
            sessionService?: string;
            notificationsService?: string;
            chatService?: string;
            gamesService?: string;
            storeService?: string;
            orderService?: string;
            cartService?: string;
            socketService?: string;
        };
    }
}

export {};
