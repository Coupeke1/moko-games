import { findOrder } from "@/features/checkout/services/checkout";
import { useQuery } from "@tanstack/react-query";

export function useOrder(id: string | undefined) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["orders", id],
        queryFn: () => findOrder(id!),
        enabled: !!id,
    });

    return { loading, error, order: data };
}
