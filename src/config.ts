export const config ={
    authUrl: window.__APP_CONFIG__?.authUrl ?? import.meta.env.VITE_AUTH_URL ?? "",
    authRealm: window.__APP_CONFIG__?.authRealm ?? import.meta.env.VITE_AUTH_REALM ?? "",
    authClientId: window.__APP_CONFIG__?.authClientId ?? import.meta.env.VITE_AUTH_CLIENT ?? "",

    ticTacToeService: window.__APP_CONFIG__?.ticTacToeService ?? import.meta.env.VITE_TIC_TAC_TOE_SERVICE ?? "",
    userService: window.__APP_CONFIG__?.userService ?? import.meta.env.VITE_USER_SERVICE ?? ""
};