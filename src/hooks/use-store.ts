import { findEntries } from "@/services/store/store.ts";
import { useQuery, useQueryClient } from "@tanstack/react-query";

export function useStore() {
    const client = useQueryClient();

    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["store"],
        queryFn: () => {
            const params = client.getQueryData<{
                query?: string;
                sorting?: string;
                category?: string;
            }>(["store", "params"]);

            return findEntries(
                params?.query,
                params?.sorting,
                params?.category,
            );
        },
    });

    return { loading, error, entries: data };
}
