import { findMyLibrary } from "@/features/profiles/services/favourites";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useAuthStore } from "@/stores/auth-store.ts";
import { useQuery } from "@tanstack/react-query";

export function useMyFavourites() {
    const { authenticated, keycloak, token } = useAuthStore();

    const {
        data,
        isLoading: loading,
        isError: error,
    } = useQuery({
        queryKey: ["library", "me", "favourites", token],
        queryFn: async () => {
            if (!authenticated || !token) throw new Error("Not authenticated");

            return await findMyLibrary({ favourite: true });
        },
        enabled: authenticated && !!keycloak && !!token,
        staleTime: 5 * 60 * 1000,
        retry: 1,
        refetchInterval: POLLING_INTERVAL,
    });

    return { favourites: data ?? [], loading, error };
}
