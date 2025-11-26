import { findIncomingRequests, findOutgoingRequests } from "@/services/friends-service";
import { useQuery } from "@tanstack/react-query";

export function useIncomingRequests() {
    const { isLoading, isError, data } = useQuery({
        queryKey: ["friends", "incoming"],
        queryFn: () => findIncomingRequests(),
    });

    return { isLoading, isError, requests: data };
}

export function useOutgoingRequests() {
    const { isLoading, isError, data } = useQuery({
        queryKey: ["friends", "outgoing"],
        queryFn: () => findOutgoingRequests(),
    });

    return { isLoading, isError, requests: data };
}