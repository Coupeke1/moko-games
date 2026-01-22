import { findGames } from "@/features/games/services/games";
import { useQuery } from "@tanstack/react-query";

export function useGames() {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({ queryKey: ["games"], queryFn: () => findGames() });

    return { loading, error, games: data ?? [] };
}
