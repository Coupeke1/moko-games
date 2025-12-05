import { isGameFavourited } from "@/services/library-service";
import { useQuery } from "@tanstack/react-query";

export function useFavourite(id: string) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["library", "favourite", id],
        queryFn: () => isGameFavourited(id),
    });

    return { loading, error, favourited: data };
}
