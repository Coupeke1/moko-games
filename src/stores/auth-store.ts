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

            keycloak.onTokenExpired = () => {
                get().updateToken(70);
            };

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
            }
            return refreshed;
        } catch (error) {
            console.error('Failed to refresh token:', error);
            return false;
        }
    }
}));