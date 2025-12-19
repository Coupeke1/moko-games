import { useQuery } from "@tanstack/react-query";
import { useAuthStore } from "@/stores/auth-store";
import { findMyAchievements } from "@/features/profile/services/achievement";
import { POLLING_INTERVAL } from "@/lib/polling";

export function useAchievements() {
    const { authenticated, keycloak, token } = useAuthStore();

    const {
        data,
        isLoading: loading,
        isError: error,
    } = useQuery({
        queryKey: ["achievements", "me", token],
        queryFn: async () => {
            if (!authenticated || !token) throw new Error("Not authenticated");

            return await findMyAchievements();
        },
        enabled: authenticated && !!keycloak && !!token,
        staleTime: 5 * 60 * 1000,
        retry: 1,
        refetchInterval: POLLING_INTERVAL,
    });

    return { achievements: data ?? [], loading, error };
}
