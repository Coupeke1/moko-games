import { findEntries } from "@/services/store-service";
import { useQuery } from "@tanstack/react-query";

export function useStore() {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["store"],
        queryFn: () => findEntries(),
    });

    return { loading, error, entries: data };
}
