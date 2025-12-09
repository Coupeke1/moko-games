import { findEntries } from "@/services/store/cart";
import { useQuery } from "@tanstack/react-query";

export function useCart(enabled: boolean) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["cart"],
        queryFn: () => findEntries(),
        enabled,
    });

    return { loading, error, entries: data };
}
