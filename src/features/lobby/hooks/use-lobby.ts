import show from "@/components/global/toast/toast";
import { Type } from "@/components/global/toast/type";
import type { Lobby, LobbyMessage } from "@/features/lobby/models/lobby.ts";
import {
    getReasonMessage,
    isClosed,
    shouldStart,
} from "@/features/lobby/services/lobby.ts";
import { isPlayerInLobby } from "@/features/lobby/services/players.ts";
import { watchLobby } from "@/features/lobby/services/socket.ts";
import { useSocketStore } from "@/stores/socket-store.ts";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router";
import { type Subscription } from "rxjs";

export function useLobby(lobbyId?: string | null, userId?: string | null) {
    const queryClient = useQueryClient();
    const navigate = useNavigate();
    const { connect, disconnect, isConnected } = useSocketStore();

    const enabled = !!lobbyId && !!userId;
    const subscription = useRef<Subscription | null>(null);
    const redirectTimeout = useRef<ReturnType<typeof setTimeout> | null>(null);
    const [isInitialized, setInitialized] = useState(false);

    const cancelRedirect = useCallback(() => {
        if (!redirectTimeout.current) return;
        clearTimeout(redirectTimeout.current);
        redirectTimeout.current = null;
    }, []);

    const scheduleRedirectToLibrary = useCallback(() => {
        if (redirectTimeout.current) return;

        redirectTimeout.current = setTimeout(() => {
            redirectTimeout.current = null;
            navigate("/library", { replace: true });
        }, 5_000);
    }, [navigate]);

    useEffect(() => {
        if (!enabled) return;
        connect();
        return () => {
            cancelRedirect();
            disconnect();
        };
    }, [enabled, connect, disconnect, cancelRedirect]);

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
        if (subscription.current && !subscription.current.closed) return;

        let isFirst = true;

        subscription.current = watchLobby().subscribe({
            next: (message: LobbyMessage) => {
                // Handle reason-based messages (kicked, etc.)
                if (message.reason) {
                    show(Type.Lobby, getReasonMessage(message.reason));
                    navigate("/library", { replace: true });
                    return;
                }

                const lobby = message.payload;
                if (!lobby) return;

                queryClient.setQueryData<Lobby>(["lobby", lobbyId], lobby);

                const kicked = !isPlayerInLobby(userId, lobby);
                const closed = isClosed(lobby);

                if (shouldStart(lobby)) {
                    cancelRedirect();
                    navigate(`/lobbies/${lobby.id}/game`, { replace: true });
                }

                if (kicked || closed) scheduleRedirectToLibrary();
                else cancelRedirect();

                if (isFirst) {
                    isFirst = false;
                    setInitialized(true);
                }
            },
            error: () => {
                subscription.current = null;
                setInitialized(false);
                scheduleRedirectToLibrary();
            },
            complete: () => {
                subscription.current = null;
                setInitialized(false);
                scheduleRedirectToLibrary();
            },
        });

        return () => {
            subscription.current?.unsubscribe();
            subscription.current = null;
            cancelRedirect();
            setInitialized(false);
        };
    }, [
        enabled,
        lobbyId,
        userId,
        isConnected,
        queryClient,
        navigate,
        scheduleRedirectToLibrary,
        cancelRedirect,
    ]);

    return {
        lobby: lobby.data ?? null,
        loading: !isInitialized && enabled,
        error: lobby.isError,
    };
}
