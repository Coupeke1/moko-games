import Keycloak, { type KeycloakConfig } from 'keycloak-js';
import { create } from 'zustand';

interface AuthState {
    keycloak: Keycloak | null;
    authenticated: boolean;
    initialized: boolean;
    token: string | null;
    init: (config: KeycloakConfig) => Promise<boolean>;
    login: () => void;
    logout: () => void;
    updateToken: (minValidity?: number) => Promise<boolean>;
    getValidToken: () => Promise<string | null>;
}

export const useAuthStore = create<AuthState>((set, get) => ({
    keycloak: null,
    authenticated: false,
    initialized: false,
    token: null,

    init: async (config: KeycloakConfig) => {
        const keycloak = new Keycloak(config);
        try {
            const authenticated = await keycloak.init({
                onLoad: "login-required",
                checkLoginIframe: false
            });

            await keycloak.updateToken(70);

            keycloak.onTokenExpired = () => {
                console.log('Token expired, refreshing...');
                get().updateToken(70).catch(() => {
                    console.error('Token refresh failed, redirecting to login');
                    get().login();
                });
            };

            setInterval(() => {
                get().updateToken(70);
            }, 30000);

            const token = keycloak.token || null;

            set({
                keycloak,
                authenticated,
                initialized: true,
                token,
            });

            return authenticated;
        } catch (error) {
            console.error(error);
            set({ initialized: true });
            return false;
        }
    },

    login: () => {
        const { keycloak } = get();
        keycloak?.login();
    },

    logout: () => {
        const { keycloak } = get();
        keycloak?.logout();
        set({
            authenticated: false,
            token: null,
        });
    },

    updateToken: async (minValidity = 70) => {
        const { keycloak } = get();
        if (!keycloak) return false;

        try {
            const refreshed = await keycloak.updateToken(minValidity);
            if (refreshed) {
                set({ token: keycloak.token || null });
                console.log('Token refreshed successfully');
            }
            return refreshed;
        } catch (error) {
            console.error('Failed to refresh token:', error);
            set({ authenticated: false });
            return false;
        }
    },

    getValidToken: async () => {
        const { keycloak, token } = get();
        if (!keycloak) return null;

        if (token && keycloak.isTokenExpired && !keycloak.isTokenExpired(70)) {
            return token;
        }

        try {
            await keycloak.updateToken(70);
            const newToken = keycloak.token || null;
            set({ token: newToken });
            return newToken;
        } catch (error) {
            console.error('Failed to get valid token:', error);
            return null;
        }
    }
}));