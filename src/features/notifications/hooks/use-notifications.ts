import { useParams } from "@/features/notifications/hooks/use-params";
import { findMyNotifications } from "@/features/notifications/services/notifications.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useAuthStore } from "@/stores/auth-store.ts";
import { useInfiniteQuery } from "@tanstack/react-query";

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
