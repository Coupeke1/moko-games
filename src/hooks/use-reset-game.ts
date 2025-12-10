import {useQueryClient} from '@tanstack/react-query';
import {resetGame} from "../services/game-service";

export function useResetGame(gameId: string) {
    const queryClient = useQueryClient();

    return async () => {
        try {
            const freshState = await resetGame(gameId);
            queryClient.setQueryData(['gameState', gameId], freshState);
        } catch (e) {
            console.error('Reset failed:', e);
        }
    };
}