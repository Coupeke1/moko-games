import { isEntryFavourited } from "@/services/library.ts";
import { useQuery } from "@tanstack/react-query";

export function useFavourite(id: string) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["library", "favourite", id],
        queryFn: () => isEntryFavourited(id),
    });

    return { loading, error, favourited: data };
}
