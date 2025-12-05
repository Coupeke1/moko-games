import { findGames } from "@/services/library-service";
import { useQuery } from "@tanstack/react-query";

export function useLibrary() {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["library"],
        queryFn: () => findGames(),
    });

    return { loading, error, games: data };
}
