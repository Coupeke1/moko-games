import {
    findIncomingRequests,
    findOutgoingRequests,
} from "@/features/friends/services/requests.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useQuery } from "@tanstack/react-query";

export function useIncomingRequests() {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["friends", "incoming"],
        queryFn: () => findIncomingRequests(),
        refetchInterval: POLLING_INTERVAL,
    });

    return { loading, error, requests: data ?? [] };
}

export function useOutgoingRequests() {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["friends", "outgoing"],
        queryFn: () => findOutgoingRequests(),
    });

    return { loading, error, requests: data ?? [] };
}
