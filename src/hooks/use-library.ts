import { findGames } from "@/services/library-service";
import { useQuery } from "@tanstack/react-query";

export function useLibrary() {
    const { isLoading, isError, data } = useQuery({
        queryKey: ["library"],
        queryFn: () => findGames()
    });

    return { isLoading, isError, games: data };
}