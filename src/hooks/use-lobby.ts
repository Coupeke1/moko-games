import { useEffect } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { firstValueFrom } from "rxjs";
import type { Lobby } from "@/models/lobby/lobby";
import { watchLobby } from "@/services/lobby/socket-service";

type UseLobbyResult = {
    lobby: Lobby | null;
    isLoading: boolean;
    isError: boolean;
    error: unknown;
};

export function useLobby(
    lobbyId?: string | null,
    userId?: string | null,
): UseLobbyResult {
    const queryClient = useQueryClient();

    const enabled = !!lobbyId && !!userId;

    const query = useQuery<Lobby>({
        queryKey: ["lobby", lobbyId],
        enabled,
        // Treat the first websocket message as the "fetch" for React Query
        queryFn: async () => {
            if (!lobbyId) {
                throw new Error("Missing lobby id");
            }
            return firstValueFrom(watchLobby(lobbyId));
        },
        staleTime: Infinity, // lobby is "live", we keep it fresh via websocket updates
    });

    useEffect(() => {
        if (!enabled || !lobbyId) return;

        // Live updates via websocket
        const subscription = watchLobby(lobbyId).subscribe({
            next: (nextLobby) => {
                queryClient.setQueryData<Lobby>(["lobby", lobbyId], nextLobby);
            },
            error: (error) => {
                console.log(error);
            },
        });

        return () => {
            subscription.unsubscribe();
        };
    }, [enabled, lobbyId, queryClient]);

    return {
        lobby: query.data ?? null,
        isLoading: query.isLoading,
        isError: query.isError,
        error: query.error,
    };
}
