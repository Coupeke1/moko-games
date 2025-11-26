import { useAuthStore } from '@/stores/auth-store';
import { useQuery } from '@tanstack/react-query';
import type {Profile} from "@/models/profile.ts";
import {parseProfile} from "@/services/profile-service.ts";

export function useMyProfile() {
    const { keycloak, authenticated, token } = useAuthStore();

    const { data: profile, isLoading, isError } = useQuery({
        queryKey: ["profile", "me", token],
        queryFn: async (): Promise<Profile> => {
            const { keycloak: freshKeycloak, token: freshToken } = useAuthStore.getState();
            if (!freshKeycloak || !freshToken)
                throw new Error("Not authenticated");

            const profile = await parseProfile(freshKeycloak, freshToken);

            if (profile === null)
                throw new Error("Could not fetch profile");

            return profile;
        },
        enabled: authenticated && !!keycloak && !!token,
        staleTime: 5 * 60 * 1000,
        retry: 1,
    });

    return { profile, isLoading, isError };
}