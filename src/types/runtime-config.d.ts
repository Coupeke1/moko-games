declare global {
    interface Window {
        __APP_CONFIG__?: {
            authUrl?: string;
            authRealm?: string;
            authClientId?: string;
            userService?: string;
            socialService?: string;
            libraryService?: string;
            sessionService?: string;
            sessionSocket?: string;
            gamesService?: string;
            storeService?: string;
            cartService?: string;
        };
    }
}

export {};
