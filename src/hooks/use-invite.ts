import { findInvites } from "@/services/lobby/lobby-service";
import { useQuery } from "@tanstack/react-query";

export function useInvites(game: string) {
    const { isLoading, isError, data } = useQuery({
        queryKey: ["lobby", "invites", game],
        queryFn: () => findInvites(game),
    });

    return { isLoading, isError, invites: data };
}
