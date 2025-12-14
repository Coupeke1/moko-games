import { useAuthStore } from "@/stores/auth-store.ts";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    getMyNotifications,
    getNotificationsByType,
    getUnreadNotifications,
    markNotificationAsRead,
} from "@/features/notifications/services/notifications.ts";
import type {
    Notification,
    Type,
    ReadFilter,
} from "@/features/notifications/models/notification.ts";

export function useNotifications(filters: {
    status: ReadFilter;
    type: Type | "all";
}) {
    const { authenticated, keycloak, token } = useAuthStore();
    const queryClient = useQueryClient();

    const {
        data: notifications,
        isLoading: loading,
        isError: error,
    } = useQuery({
        queryKey: ["notifications", "me", filters],
        queryFn: async (): Promise<Notification[]> => {
            let data: Notification[];

            if (filters.status === "unread") {
                data = await getUnreadNotifications();

                if (filters.type !== "all") {
                    data = data.filter((n) => n.type === filters.type);
                }
                return data;
            }

            if (filters.type !== "all") {
                data = await getNotificationsByType(filters.type);

                if (filters.status === "read") {
                    data = data.filter((n) => n.read);
                }
                return data;
            }

            data = await getMyNotifications();

            if (filters.status === "read") return data.filter((n) => n.read);
            return data;
        },
        enabled: authenticated && !!keycloak && !!token,
        staleTime: 30 * 1000,
        retry: 1,
    });

    const markAsReadMutation = useMutation({
        mutationFn: async (id: string) => markNotificationAsRead(id),
        onSuccess: async () => {
            await queryClient.invalidateQueries({
                queryKey: ["notifications"],
            });
        },
    });

    return {
        notifications,
        loading,
        error,
        markAsRead: markAsReadMutation.mutate,
        marking: markAsReadMutation.isPending,
    };
}
