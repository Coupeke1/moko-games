import { isEntryFavourited } from "@/features/library/services/library.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useQuery } from "@tanstack/react-query";

export function useFavourite(id: string) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["library", "favourite", id],
        queryFn: () => isEntryFavourited(id),
        refetchInterval: POLLING_INTERVAL,
    });

    return { loading, error, favourited: data ?? null };
}
