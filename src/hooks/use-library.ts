import { findEntries } from "@/services/library.ts";
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
    });

    return { loading, error, entries: data };
}
