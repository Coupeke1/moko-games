import { findEntry } from "@/features/store/services/store.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useQuery } from "@tanstack/react-query";

export function useEntry(id: string | undefined) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["store", id],
        queryFn: () => findEntry(id!),
        enabled: !!id,
        refetchInterval: POLLING_INTERVAL,
    });

    return { loading, error, entry: data };
}
