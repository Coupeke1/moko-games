export const environment = {
    authUrl:
        window.__APP_CONFIG__?.authUrl ?? import.meta.env.VITE_AUTH_URL ?? "",
    authRealm:
        window.__APP_CONFIG__?.authRealm ??
        import.meta.env.VITE_AUTH_REALM ??
        "",
    authClientId:
        window.__APP_CONFIG__?.authClientId ??
        import.meta.env.VITE_AUTH_CLIENT ??
        "",

    userService:
        window.__APP_CONFIG__?.userService ??
        import.meta.env.VITE_USER_SERVICE ??
        "",
    achievementService:
        window.__APP_CONFIG__?.achievementService ??
        import.meta.env.VITE_ACHIEVEMENT_SERVICE ??
        "",
    socialService:
        window.__APP_CONFIG__?.socialService ??
        import.meta.env.VITE_SOCIAL_SERVICE ??
        "",
    libraryService:
        window.__APP_CONFIG__?.libraryService ??
        import.meta.env.VITE_LIBRARY_SERVICE ??
        "",
    sessionService:
        window.__APP_CONFIG__?.sessionService ??
        import.meta.env.VITE_SESSION_SERVICE ??
        "",
    notificationsService:
        window.__APP_CONFIG__?.notificationsService ??
        import.meta.env.VITE_NOTIFICATIONS_SERVICE ??
        "",
    chatService:
        window.__APP_CONFIG__?.chatService ??
        import.meta.env.VITE_CHAT_SERVICE ??
        "",
    gamesService:
        window.__APP_CONFIG__?.gamesService ??
        import.meta.env.VITE_GAMES_SERVICE ??
        "",
    storeService:
        window.__APP_CONFIG__?.storeService ??
        import.meta.env.VITE_STORE_SERVICE ??
        "",
    orderService:
        window.__APP_CONFIG__?.orderService ??
        import.meta.env.VITE_ORDER_SERVICE ??
        "",
    cartService:
        window.__APP_CONFIG__?.cartService ??
        import.meta.env.VITE_CART_SERVICE ??
        "",
    socketService:
        window.__APP_CONFIG__?.socketService ??
        import.meta.env.VITE_SOCKET_SERVICE ??
        "",
};
