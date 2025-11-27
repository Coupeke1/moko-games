import {getGameState} from "@/routes/game/services/game-service.ts";
import {useQuery} from "@tanstack/react-query";

export function useGameState(id: string) {
    return useQuery({
        queryKey: ['gameState', id],
        queryFn: () => getGameState(id),
        refetchInterval: 1000,
        staleTime: 1000,
        enabled: !!id,
        retry: 3,
    })
}