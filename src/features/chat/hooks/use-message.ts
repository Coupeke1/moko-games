import type { Message } from "@/features/chat/models/message";
import { watchChat } from "@/features/chat/services/socket";
import { useSocketStore } from "@/stores/socket-store.ts";
import { useEffect, useRef, useState } from "react";
import { type Subscription } from "rxjs";

export function useMessage(id?: string | null) {
    const { connect, disconnect, isConnected } = useSocketStore();
    const subscription = useRef<Subscription | null>(null);

    const [message, setMessage] = useState<Message | null>(null);
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
            next: (message) => setMessage(message),
            error: () => (subscription.current = null),
            complete: () => (subscription.current = null),
        });

        return () => {
            subscription.current?.unsubscribe();
            subscription.current = null;
        };
    }, [enabled, id, isConnected]);

    return { message, loading: false, error: false };
}
