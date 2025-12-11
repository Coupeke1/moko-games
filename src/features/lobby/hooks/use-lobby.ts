import type { Lobby } from "@/features/lobby/models/lobby.ts";
import { isClosed, shouldStart } from "@/features/lobby/services/lobby.ts";
import { isPlayerInLobby } from "@/features/lobby/services/players.ts";
import { watchLobby } from "@/features/lobby/services/socket.ts";
import { useSocketStore } from "@/stores/socket-store.ts";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router";
import { type Subscription } from "rxjs";

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

                if (isClosed(lobby)) navigate("/library");
                if (!isPlayerInLobby(userId, lobby)) navigate("/library");
                if (shouldStart(lobby)) navigate(`/lobbies/${lobby.id}/game`);

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
