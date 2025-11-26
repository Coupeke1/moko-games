import type {Profile} from "@/models/profile.ts";
import {useQueryClient} from "@tanstack/react-query";
import {requestMove} from "@/routes/game/services/game-service.ts";

export function useMakeMove(gameId: string, profile: Profile | undefined) {
    const queryClient = useQueryClient();

    const makeMove = async (row: number, col: number) => {
        if (!profile) return;

        try {
            const updatedGameState = await requestMove(gameId, profile.id, row, col);
            queryClient.setQueryData(['gameState', gameId], updatedGameState);
        } catch (err) {
            console.error('Move failed:', err);
        }
    };

    return makeMove;
}