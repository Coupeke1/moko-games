import { useEffect, useRef } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { firstValueFrom, type Subscription } from "rxjs";
import type { Lobby } from "@/models/lobby/lobby";
import { watchLobby } from "@/services/lobby/socket-service";
import { useSocketStore } from "@/stores/socket-store";

export function useLobby(lobbyId?: string | null, userId?: string | null) {
    const queryClient = useQueryClient();
    const { connect, disconnect } = useSocketStore();
    const enabled = !!lobbyId && !!userId;

    // Track the subscription to prevent duplicates
    const subscriptionRef = useRef<Subscription | null>(null);

    // Establish socket connection when hook is used
    useEffect(() => {
        if (!enabled) return;

        connect();
        return () => {
            disconnect();
        };
    }, [enabled, connect, disconnect]);

    // Fetch initial data ONCE
    const query = useQuery<Lobby>({
        queryKey: ["lobby", lobbyId],
        enabled,
        queryFn: async () => {
            if (!lobbyId) throw new Error("Missing lobby id");
            // Get initial snapshot
            return firstValueFrom(watchLobby(lobbyId));
        },
        staleTime: Infinity,
        refetchOnMount: false,
        refetchOnWindowFocus: false,
        refetchOnReconnect: false,
    });

    // Subscribe to updates AFTER initial fetch
    useEffect(() => {
        if (!enabled || !lobbyId || !query.data) return;

        // Prevent duplicate subscriptions
        if (subscriptionRef.current) {
            return;
        }

        subscriptionRef.current = watchLobby(lobbyId).subscribe({
            next: (nextLobby) => {
                queryClient.setQueryData<Lobby>(["lobby", lobbyId], nextLobby);
            },
            error: (error) => {
                console.error("Lobby subscription error:", error);
            },
        });

        return () => {
            if (subscriptionRef.current) {
                subscriptionRef.current.unsubscribe();
                subscriptionRef.current = null;
            }
        };
    }, [enabled, lobbyId, query.data, queryClient]);

    return {
        lobby: query.data ?? null,
        isLoading: query.isLoading,
        isError: query.isError,
    };
}
