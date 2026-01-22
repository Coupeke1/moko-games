import { findInvites } from "@/features/lobby/services/invites.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useQuery } from "@tanstack/react-query";

export function useInvites(id: string) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["lobby", "invites", id],
        queryFn: () => findInvites(id),
        refetchInterval: POLLING_INTERVAL,
    });

    return { loading, error, invites: data ?? [] };
}
