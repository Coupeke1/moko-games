import type { Profile } from '@/models/profile';
import { parseProfile } from '@/services/profile-service';
import { useAuthStore } from '@/stores/auth-store';
import { useQuery } from '@tanstack/react-query';

export function useMyProfile() {
    const { keycloak, authenticated, token } = useAuthStore();

    return useQuery({
        queryKey: ['profile', token],
        queryFn: async (): Promise<Profile> => {
            if (!keycloak || !token)
                throw new Error("Not authenticated");

            const profile = await parseProfile(keycloak, token);

            if (profile === null)
                throw new Error("Could not fetch profile");

            return profile;
        },
        enabled: authenticated && !!keycloak && !!token,
        staleTime: 5 * 60 * 1000,
        retry: 1,
    });
};