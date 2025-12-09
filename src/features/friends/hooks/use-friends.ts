import { findFriends } from "@/features/friends/services/friends.ts";
import { useQuery } from "@tanstack/react-query";

export function useFriends() {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["friends"],
        queryFn: () => findFriends(),
    });

    return { loading, error, friends: data };
}
