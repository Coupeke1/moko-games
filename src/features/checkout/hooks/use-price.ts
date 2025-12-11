import { getTotalPrice } from "@/features/cart/services/cart.ts";
import { useQuery } from "@tanstack/react-query";

export function usePrice() {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["cart", "price"],
        queryFn: () => getTotalPrice(),
    });

    return { loading, error, price: data };
}
