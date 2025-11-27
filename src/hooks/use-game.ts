import { findGame } from "@/services/game-service";
import { useQuery } from "@tanstack/react-query";

export function useGame(id: string) {
    const { isLoading, isError, data } = useQuery({
        queryKey: ["games", id],
        queryFn: () => findGame(id)
    });

    return { isLoading, isError, game: data };
}