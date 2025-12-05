import { findFriends } from "@/services/friends-service";
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
