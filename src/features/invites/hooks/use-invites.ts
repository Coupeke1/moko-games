import { findInvites } from "@/features/lobby/services/invites.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useQuery } from "@tanstack/react-query";

export function useInvites(game: string) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["lobby", "invites", game],
        queryFn: () => findInvites(game),
        refetchInterval: POLLING_INTERVAL,
    });

    return { loading, error, invites: data ?? [] };
}
