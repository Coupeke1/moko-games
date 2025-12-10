import type {Profile} from "@/models/profile.ts";
import {useQueryClient} from "@tanstack/react-query";
import {requestMove} from "@/routes/game/services/game-service.ts";
import {useState} from "react";
import {GameStatus} from "@/routes/game/model/game-status.ts";

export function useMakeMove(gameId: string, profile: Profile | undefined, status: GameStatus | undefined) {
    const queryClient = useQueryClient();
    const [errorMsg, setErrorMsg] = useState<string | null>(null);

    const makeMove = async (cells: number[]) => {
        if (!status || status !== GameStatus.RUNNING) return;
        if (!profile) return;
        if (cells.length < 2) return;

        try {
            const updatedGameState = await requestMove(gameId, profile.id, cells);
            queryClient.setQueryData(['gameState', gameId], updatedGameState);
        } catch (error) {
            if (error instanceof Error) {
                setErrorMsg(error.message);
            }
            setErrorMsg('Illegal move');
        }
    };

    const closeToast = () => setErrorMsg(null);

    return { makeMove, errorMsg, closeToast };
}