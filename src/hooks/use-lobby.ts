import { useEffect, useRef } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { type Subscription } from "rxjs";
import type { Lobby } from "@/models/lobby/lobby";
import { watchLobby } from "@/services/lobby/socket-service";
import { useSocketStore } from "@/stores/socket-store";

export function useLobby(lobbyId?: string | null, userId?: string | null) {
    const queryClient = useQueryClient();
    const { connect, disconnect, isConnected } = useSocketStore();
    const enabled = !!lobbyId && !!userId;

    const subscriptionRef = useRef<Subscription | null>(null);
    const isInitializedRef = useRef(false);

    // Establish socket connection when hook is used
    useEffect(() => {
        if (!enabled) return;
        connect();
        return () => {
            disconnect();
        };
    }, [enabled, connect, disconnect]);

    // Don't use React Query for fetching - just for storing data
    const query = useQuery<Lobby>({
        queryKey: ["lobby", lobbyId],
        enabled: false,
        queryFn: async () => {
            throw new Error("Should not be called");
        },
        staleTime: Infinity,
    });

    // Subscribe to lobby updates ONLY after connection is established
    useEffect(() => {
        if (!enabled || !lobbyId || !isConnected) return;

        // Prevent duplicate subscriptions
        if (subscriptionRef.current) {
            return;
        }

        let isFirst = true;

        subscriptionRef.current = watchLobby(lobbyId).subscribe({
            next: (nextLobby) => {
                console.log("Received lobby update:", nextLobby);
                queryClient.setQueryData<Lobby>(["lobby", lobbyId], nextLobby);
                if (isFirst) {
                    isFirst = false;
                    isInitializedRef.current = true;
                }
            },
            error: (error) => {
                console.error("Lobby subscription error:", error);
                isInitializedRef.current = false;
            },
        });

        return () => {
            if (subscriptionRef.current) {
                subscriptionRef.current.unsubscribe();
                subscriptionRef.current = null;
            }
            isInitializedRef.current = false;
        };
    }, [enabled, lobbyId, isConnected, queryClient]); // Added isConnected dependency

    return {
        lobby: query.data ?? null,
        isLoading: !isInitializedRef.current && enabled,
        isError: query.isError,
    };
}
