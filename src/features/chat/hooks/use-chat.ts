import type { Message } from "@/features/chat/models/message";
import { watchChat } from "@/features/chat/services/socket";
import { useSocketStore } from "@/stores/socket-store.ts";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router";
import { type Subscription } from "rxjs";

export function useChat(chatId?: string | null) {
    const queryClient = useQueryClient();
    const navigate = useNavigate();
    const { connect, disconnect, isConnected } = useSocketStore();

    const enabled = !!chatId;
    const subscription = useRef<Subscription | null>(null);
    const [isInitialized, setInitialized] = useState(false);

    useEffect(() => {
        if (!enabled) return;
        connect();
        return () => disconnect();
    }, [enabled, connect, disconnect]);

    const message = useQuery<Message>({
        queryKey: ["chat", chatId],
        enabled: false,
        queryFn: async () => {
            throw new Error("Should not be called");
        },
        staleTime: Infinity,
    });

    useEffect(() => {
        if (!enabled || !chatId || !isConnected) return;
        if (subscription.current && !subscription.current.closed) return;

        let isFirst = true;

        subscription.current = watchChat().subscribe({
            next: (message) => {
                queryClient.setQueryData<Message>(["chat", chatId], message);

                if (isFirst) {
                    isFirst = false;
                    setInitialized(true);
                }
            },
            error: () => {
                subscription.current = null;
                setInitialized(false);
            },
            complete: () => {
                subscription.current = null;
                setInitialized(false);
            },
        });

        return () => {
            subscription.current?.unsubscribe();
            subscription.current = null;
            setInitialized(false);
        };
    }, [enabled, chatId, isConnected, queryClient, navigate]);

    return {
        message: message.data ?? null,
        loading: !isInitialized && enabled,
        error: message.isError,
    };
}
