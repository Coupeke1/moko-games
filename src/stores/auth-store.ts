import Keycloak from 'keycloak-js';
import { create } from 'zustand';

interface AuthState {
    keycloak: Keycloak | null;
    authenticated: boolean;
    initialized: boolean;
    user: {
        username?: string;
        email?: string;
        firstName?: string;
        lastName?: string;
        roles?: string[];
    } | null;
    token: string | null;

    init: (config: Keycloak.KeycloakConfig) => Promise<boolean>;
    login: () => void;
    logout: () => void;
    register: () => void;
    updateToken: (minValidity?: number) => Promise<boolean>;
    hasRole: (role: string) => boolean;
    hasRealmRole: (role: string) => boolean;
    hasResourceRole: (role: string, resource: string) => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
    keycloak: null,
    authenticated: false,
    initialized: false,
    user: null,
    token: null,

    init: async (config: Keycloak.KeycloakConfig) => {
        const keycloak = new Keycloak(config);

        try {
            const authenticated = await keycloak.init({ onLoad: "login-required", checkLoginIframe: false });

            keycloak.onTokenExpired = () => {
                get().updateToken(70);
            };

            set({
                keycloak,
                authenticated,
                initialized: true,
                token: keycloak.token || null,
                user: authenticated ? {
                    username: keycloak.tokenParsed?.preferred_username,
                    email: keycloak.tokenParsed?.email,
                    firstName: keycloak.tokenParsed?.given_name,
                    lastName: keycloak.tokenParsed?.family_name,
                    roles: keycloak.realmAccess?.roles || [],
                } : null,
            });

            return authenticated;
        } catch (error) {
            console.error('Failed to initialize Keycloak:', error);
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
            user: null,
            token: null,
        });
    },

    register: () => {
        const { keycloak } = get();
        keycloak?.register();
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
    },

    hasRole: (role: string) => {
        const { keycloak } = get();
        return keycloak?.hasRealmRole(role) || false;
    },

    hasRealmRole: (role: string) => {
        const { keycloak } = get();
        return keycloak?.hasRealmRole(role) || false;
    },

    hasResourceRole: (role: string, resource: string) => {
        const { keycloak } = get();
        return keycloak?.hasResourceRole(role, resource) || false;
    },
}));