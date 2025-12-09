import { findInvites } from "@/services/lobby/lobby.ts";
import { useQuery } from "@tanstack/react-query";

export function useInvites(game: string) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["lobby", "invites", game],
        queryFn: () => findInvites(game),
    });

    return { loading, error, invites: data };
}
