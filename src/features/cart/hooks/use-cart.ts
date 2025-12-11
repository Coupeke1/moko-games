import { findEntries } from "@/features/cart/services/cart.ts";
import { useQuery } from "@tanstack/react-query";

export function useCart(enabled?: boolean) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["cart"],
        queryFn: () => findEntries(),
        enabled: enabled ?? true,
    });

    return { loading, error, entries: data };
}
