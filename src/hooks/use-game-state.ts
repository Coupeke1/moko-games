import {getGameState} from "@/services/game-service";
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