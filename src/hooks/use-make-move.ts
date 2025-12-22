import {toast} from "sonner";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {requestMove} from "@/services/game-service.ts";
import {useState} from "react";

export function useMakeMove(gameId: string | undefined, playerId: string | undefined) {
    const queryClient = useQueryClient();
    const [isSubmitting, setIsSubmitting] = useState(false);

    const moveMutation = useMutation({
        mutationFn: (cells: number[]) => {
            if (!gameId) {
                throw new Error("Game ID missing");
            }
            if (!playerId) {
                throw new Error("Player ID missing");
            }
            return requestMove(gameId, playerId, cells);
        },
        onMutate: () => {
            setIsSubmitting(true);
        },
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['gameState', gameId]});
            toast.success("Move successful!");
        },
        onError: (error: Error) => {
            toast.error(`Move failed: ${error.message}`);
        },
        onSettled: () => {
            setIsSubmitting(false);
        }
    });

    const confirmMove = (movePath: number[], onSuccess?: () => void) => {
        if (movePath.length < 2) {
            toast.error("Select at least 2 cells to make a move");
            return false;
        }

        if (!gameId || !playerId) {
            toast.error("Cannot make move. Please try again.");
            return false;
        }

        moveMutation.mutate(movePath, {
            onSuccess: () => {
                onSuccess?.();
            }
        });

        return true;
    }

    return {
        confirmMove,
        isSubmitting: isSubmitting || moveMutation.isPending,
        moveMutation
    }
}