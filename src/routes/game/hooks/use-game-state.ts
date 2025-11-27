import {getGameState} from "@/routes/game/services/game-service.ts";
import {useQuery} from "@tanstack/react-query";

export function useGameState(id: string) {
    return useQuery({
        queryKey: ['gameState', id],
        queryFn: () => getGameState(id),
        refetchInterval: 2000,
        staleTime: 5000,
        enabled: !!id,
        retry: 3,
    })
}