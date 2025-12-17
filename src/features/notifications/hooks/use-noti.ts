import { useParams } from "@/features/notifications/hooks/use-params";
import type { Notification } from "@/features/notifications/models/notification";
import {
    findMyNotifications,
    findMyNotificationsSince,
} from "@/features/notifications/services/notifications.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useAuthStore } from "@/stores/auth-store.ts";
import {
    useInfiniteQuery,
    useQuery,
    useQueryClient,
} from "@tanstack/react-query";
import { useRef } from "react";

export function useNotifications() {
    const { authenticated, keycloak, token } = useAuthStore();
    const { type, origin } = useParams();

    const {
        data,
        isLoading: loading,
        isError: error,
        fetchNextPage,
        hasNextPage,
        isFetchingNextPage: fetching,
    } = useInfiniteQuery({
        queryKey: ["notifications", type, origin],
        queryFn: ({ pageParam = 0 }) =>
            findMyNotifications(type, origin, pageParam),
        getNextPageParam: (lastPage) => {
            if (lastPage.items.length > 0 && !lastPage.last)
                return lastPage.page + 1;
            return undefined;
        },
        enabled: authenticated && !!keycloak && !!token,
        staleTime: 30 * 1000,
        retry: 1,
        refetchInterval: POLLING_INTERVAL,
    });

    return {
        notifications: data?.pages.flatMap((page) => page.items) ?? [],
        loading,
        fetching,
        error,
        fetchNextPage,
        hasNextPage,
    };
}

export function useNotificationsSince() {
    const { authenticated, keycloak, token } = useAuthStore();
    const client = useQueryClient();

    const latestTimeRef = useRef<string>(new Date(0).toISOString());

    const {
        data = [],
        isLoading,
        isError,
        isFetching,
    } = useQuery({
        queryKey: ["notifications", "since"],
        queryFn: async () => {
            const notifications = await findMyNotificationsSince(
                latestTimeRef.current,
            );

            if (notifications.length > 0) {
                const newest = notifications.reduce((a, b) =>
                    a.createdAt > b.createdAt ? a : b,
                );
                latestTimeRef.current = newest.createdAt;
            }

            return notifications;
        },
        enabled: authenticated && !!keycloak && !!token,
        refetchInterval: POLLING_INTERVAL,
        staleTime: 0,
        retry: 1,
        select: (newNotifications) => {
            const existing =
                client.getQueryData<Notification[]>(["notifications"]) ?? [];

            const merged = [...newNotifications, ...existing];
            const unique = Array.from(
                new Map(merged.map((n) => [n.id, n])).values(),
            );

            return unique.sort(
                (a, b) =>
                    new Date(b.createdAt).getTime() -
                    new Date(a.createdAt).getTime(),
            );
        },
    });

    return {
        notifications: data,
        loading: isLoading,
        fetching: isFetching,
        error: isError,
    };
}
