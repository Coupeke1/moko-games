import type { Message } from "@/features/chat/models/message";
import { watchChat } from "@/features/chat/services/socket";
import { useSocketStore } from "@/stores/socket-store";
import { useQueryClient } from "@tanstack/react-query";
import { useEffect, useRef } from "react";
import type { Subscription } from "rxjs";

export function useMessage(id?: string | null) {
    const { connect, disconnect, isConnected } = useSocketStore();
    const subscription = useRef<Subscription | null>(null);
    const queryClient = useQueryClient();

    const enabled = !!id;

    useEffect(() => {
        if (!enabled) return;
        connect();
        return () => disconnect();
    }, [enabled, connect, disconnect]);

    useEffect(() => {
        if (!enabled || !id || !isConnected) return;
        if (subscription.current && !subscription.current.closed) return;

        subscription.current = watchChat(id).subscribe({
            next: (message: Message) => {
                queryClient.setQueryData<Message[]>(
                    ["chat", id, "messages"],
                    (old = []) => {
                        if (old.some((m) => m.id === message.id)) return old;
                        return [...old, message];
                    },
                );
            },
            error: () => (subscription.current = null),
            complete: () => (subscription.current = null),
        });

        return () => {
            subscription.current?.unsubscribe();
            subscription.current = null;
        };
    }, [enabled, id, isConnected, queryClient]);

    return null;
}
