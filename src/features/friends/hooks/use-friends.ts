import { findFriends } from "@/features/friends/services/friends.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useQuery } from "@tanstack/react-query";

export function useFriends() {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["friends"],
        queryFn: () => findFriends(),
        refetchInterval: POLLING_INTERVAL,
    });

    return { loading, error, friends: data };
}
