import {useQuery} from "@tanstack/react-query";
import {getGameState} from "../services/game-service";

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