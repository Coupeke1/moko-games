import { useEffect, useRef, useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { type Subscription } from "rxjs";
import type { Lobby } from "@/models/lobby/lobby";
import { watchLobby } from "@/services/lobby/socket.ts";
import { useSocketStore } from "@/stores/socket-store";
import { isPlayerInLobby } from "@/services/lobby/lobby.ts";
import { useNavigate } from "react-router";

export function useLobby(lobbyId?: string | null, userId?: string | null) {
    const queryClient = useQueryClient();
    const navigate = useNavigate();
    const { connect, disconnect, isConnected } = useSocketStore();

    const enabled = !!lobbyId && !!userId;
    const subscription = useRef<Subscription | null>(null);
    const [isInitialized, setInitialized] = useState(false);

    useEffect(() => {
        if (!enabled) return;
        connect();
        return () => disconnect();
    }, [enabled, connect, disconnect]);

    const lobby = useQuery<Lobby>({
        queryKey: ["lobby", lobbyId],
        enabled: false,
        queryFn: async () => {
            throw new Error("Should not be called");
        },
        staleTime: Infinity,
    });

    useEffect(() => {
        if (!enabled || !lobbyId || !isConnected) return;

        if (subscription.current) return;
        let isFirst = true;

        subscription.current = watchLobby(lobbyId).subscribe({
            next: (lobby) => {
                queryClient.setQueryData<Lobby>(["lobby", lobbyId], lobby);
                if (!isPlayerInLobby(userId, lobby)) navigate("/library");

                if (isFirst) {
                    isFirst = false;
                    setInitialized(true);
                }
            },
            error: () => setInitialized(false),
        });

        return () => {
            if (subscription.current) {
                subscription.current.unsubscribe();
                subscription.current = null;
            }

            setInitialized(false);
        };
    }, [enabled, lobbyId, userId, isConnected, queryClient, navigate]);

    return {
        lobby: lobby.data ?? null,
        loading: !isInitialized && enabled,
        error: lobby.isError,
    };
}
