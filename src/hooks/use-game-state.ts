import {getGameState} from "@/services/game-service";
import {useQuery} from "@tanstack/react-query";

export function useGameState(id: string) {
    return useQuery({
        queryKey: ['gameState', id],
        queryFn: () => getGameState(id),
        refetchInterval: 5000,
        staleTime: 5000,
        retry: 3,
    })
}