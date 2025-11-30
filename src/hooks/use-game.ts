import { findGame } from "@/services/game-service";
import { useQuery } from "@tanstack/react-query";

export function useGame(id: string | undefined) {
    const { isLoading, isError, data } = useQuery({
        queryKey: ["games", id],
        queryFn: () => findGame(id!),
        enabled: !!id,
    });

    return { isLoading, isError, game: data };
}
