import type { Notification } from "@/features/notifications/models/notification";
import { watchNotifications } from "@/features/notifications/services/socket";
import { useSocketStore } from "@/stores/socket-store.ts";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router";
import { type Subscription } from "rxjs";

export function useNotification() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();
    const { connect, disconnect, isConnected } = useSocketStore();

    const subscription = useRef<Subscription | null>(null);
    const [isInitialized, setInitialized] = useState(false);

    useEffect(() => {
        connect();
        return () => disconnect();
    }, [connect, disconnect]);

    const notifications = useQuery<Notification[]>({
        queryKey: ["notifications", "me"],
        enabled: false,
        queryFn: async () => {
            throw new Error("Should not be called");
        },
        staleTime: Infinity,
    });

    useEffect(() => {
        if (!isConnected) return;

        if (subscription.current) return;
        let isFirst = true;

        subscription.current = watchNotifications().subscribe({
            next: (notification) => {
                queryClient.setQueryData<Notification[]>(
                    ["notifications", "me"],
                    (old = []) => [...old, notification],
                );

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
    }, [isConnected, queryClient, navigate]);

    return {
        notifications: notifications.data ?? null,
        loading: !isInitialized,
        error: notifications.isError,
    };
}
