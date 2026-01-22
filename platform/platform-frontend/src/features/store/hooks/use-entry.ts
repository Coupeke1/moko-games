import { findEntry } from "@/features/store/services/store.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useQuery } from "@tanstack/react-query";

export function useEntry(id: string | undefined) {
    const query = useQuery({
        queryKey: ["store", id],
        queryFn: () => findEntry(id as string),
        enabled: Boolean(id),
        refetchInterval: POLLING_INTERVAL,
    });

    return {
        entry: query.data ?? null,
        loading: query.isLoading && Boolean(id),
        error: query.isError,
    };
}
