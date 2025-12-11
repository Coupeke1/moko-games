import { verifyOrder } from "@/features/checkout/services/checkout";
import { useQuery } from "@tanstack/react-query";

export function useVerify(id: string | undefined) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["orders", id],
        queryFn: () => verifyOrder(id!),
        enabled: !!id,
    });

    return { loading, error, order: data };
}
