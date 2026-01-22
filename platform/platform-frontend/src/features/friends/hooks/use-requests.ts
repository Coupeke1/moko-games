import { findIncomingRequests } from "@/features/friends/services/requests.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useQuery } from "@tanstack/react-query";

export function useRequests() {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["friends", "requests"],
        queryFn: () => findIncomingRequests(),
        refetchInterval: POLLING_INTERVAL,
    });

    return { loading, error, requests: data ?? [] };
}
