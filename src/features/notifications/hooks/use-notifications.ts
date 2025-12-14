import { findMyNotifications } from "@/features/notifications/services/notifications.ts";
import type { Notifications } from "@/features/notifications/models/notifications";
import { useAuthStore } from "@/stores/auth-store.ts";
import { useQuery, useQueryClient } from "@tanstack/react-query";

export function useNotifications() {
    const client = useQueryClient();
    const { authenticated, keycloak, token } = useAuthStore();

    const {
        data: notifications,
        isLoading: loading,
        isError: error,
    } = useQuery({
        queryKey: ["notifications"],
        queryFn: async (): Promise<Notifications> => {
            const params = client.getQueryData<{
                type?: string;
                origin?: string;
            }>(["notifications", "params"]);

            return findMyNotifications(params?.type, params?.origin);
        },
        enabled: authenticated && !!keycloak && !!token,
        staleTime: 30 * 1000,
        retry: 1,
    });

    return { notifications, loading, error };
}
