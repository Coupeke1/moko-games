import {useQuery} from "@tanstack/react-query";
import {useAuthStore} from "@/stores/auth-store";
import {findMyLibrary} from "@/features/profile/services/favourites";

export function useFavourites() {
    const {authenticated, keycloak, token} = useAuthStore();

    const {
        data: games,
        isLoading: loading,
        isError: error,
    } = useQuery({
        queryKey: ["library", "me", "favourites", token],
        queryFn: async () => {
            if (!authenticated || !token)
                throw new Error("Not authenticated");

            return await findMyLibrary({ favourite: true });
        },
        enabled: authenticated && !!keycloak && !!token,
        staleTime: 5 * 60 * 1000,
        retry: 1,
    });

    const favourites = games ? { games } : undefined;
    console.log("favourites");
    console.log(favourites);
    return {favourites, loading, error};
}