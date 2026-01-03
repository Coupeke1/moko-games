import {useMutation, useQueryClient} from "@tanstack/react-query";
import {useState} from "react";
import {requestBotMove} from "@/services/game-service.ts";
import {toast} from "sonner";

export function useBotMove(gameId: string | undefined) {
    const queryClient = useQueryClient();
    const [isBotMoving, setIsBotMoving] = useState(false);

    const botMoveMutation = useMutation({
        mutationFn: () => {
            if (!gameId) {
                throw new Error("Game ID missing");
            }
            return requestBotMove(gameId);
        },
        onMutate: () => {
            setIsBotMoving(true);
        },
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['gameState', gameId]});
            toast.success("Bot move completed!");
        },
        onError: (error: Error) => {
            toast.error(`Bot move failed: ${error.message}`);
        },
        onSettled: () => {
            setIsBotMoving(false);
        }
    });

    const requestBotTurn = () => {
        if (!gameId) {
            toast.error("Cannot request bot move. Game ID missing.");
            return false;
        }

        botMoveMutation.mutate();
        return true;
    }

    return {
        requestBotTurn,
        isBotMoving: isBotMoving || botMoveMutation.isPending,
        botMoveMutation
    }
}