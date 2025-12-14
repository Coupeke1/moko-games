import { findEntries } from "@/features/library/services/library.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useQuery, useQueryClient } from "@tanstack/react-query";

export function useLibrary() {
    const client = useQueryClient();

    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["library"],
        queryFn: () => {
            const params = client.getQueryData<{ query?: string }>([
                "library",
                "params",
            ]);

            return findEntries(params?.query);
        },
        refetchInterval: POLLING_INTERVAL,
    });

    return { loading, error, entries: data };
}
