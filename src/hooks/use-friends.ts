import { findFriends } from "@/services/friends-service";
import { useQuery } from "@tanstack/react-query";

export function useFriends() {
    const { isLoading, isError, data } = useQuery({
        queryKey: ["friends"],
        queryFn: () => findFriends(),
    });

    return { isLoading, isError, friends: data };
}