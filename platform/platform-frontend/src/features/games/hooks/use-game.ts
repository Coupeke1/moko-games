import { findGame } from "@/features/games/services/games";
import { useQuery } from "@tanstack/react-query";

export function useGame(id: string | undefined) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["games", id],
        queryFn: () => findGame(id!),
        enabled: !!id,
    });

    return { loading, error, game: data ?? null };
}
