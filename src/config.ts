export const config = {
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
    socialService:
        window.__APP_CONFIG__?.socialService ??
        import.meta.env.VITE_SOCIAL_SERVICE ??
        "",
    libraryService:
        window.__APP_CONFIG__?.libraryService ??
        import.meta.env.VITE_LIBRARY_SERVICE ??
        "",
};
