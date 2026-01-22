declare global {
    interface Window {
        __APP_CONFIG__?: {
            authUrl?: string;
            authRealm?: string;
            authClientId?: string;
            ticTacToeService?: string;
            userService?: string;
        };
    }
}

export {};