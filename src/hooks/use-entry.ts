import { findEntry } from "@/services/store/store.ts";
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
    });

    return { loading, error, entry: data };
}
