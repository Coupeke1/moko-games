import { findEntries } from "@/features/cart/services/cart.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
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
        refetchInterval: POLLING_INTERVAL,
    });

    return { loading, error, entries: data ?? [] };
}
