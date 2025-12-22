import type { Me } from "@/features/profile/models/me";
import { parseProfile } from "@/features/profile/services/profile.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useAuthStore } from "@/stores/auth-store.ts";
import { useQuery } from "@tanstack/react-query";

export function useProfile() {
    const { keycloak, authenticated, token } = useAuthStore();

    const {
        data: profile,
        isLoading: loading,
        isError: error,
    } = useQuery({
        queryKey: ["profile", "me"],
        queryFn: async (): Promise<Me> => {
            const { keycloak: freshKeycloak, token: freshToken } =
                useAuthStore.getState();

            if (!freshKeycloak || !freshToken)
                throw new Error("Not authenticated");

            const profile = await parseProfile(freshKeycloak, freshToken);

            if (profile === null) throw new Error("Could not fetch profile");

            return profile;
        },
        enabled: authenticated && !!keycloak && !!token,
        staleTime: 5 * 60 * 1000,
        retry: 1,
        refetchInterval: POLLING_INTERVAL,
    });

    return { loading, error, profile };
}
