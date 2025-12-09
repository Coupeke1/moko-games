import { hasEntry as hasEntryInCart } from "@/features/cart/services/cart";
import { hasEntry as hasEntryInLibrary } from "@/features/library/services/library";
import { useQuery } from "@tanstack/react-query";

export function useAvailable(id: string | undefined) {
    const {
        isLoading: cartLoading,
        isError: cartError,
        data: cartData,
    } = useQuery({
        queryKey: ["cart", id],
        queryFn: () => hasEntryInCart(id!),
        enabled: !!id,
    });

    const {
        isLoading: libraryLoading,
        isError: libraryError,
        data: libraryData,
    } = useQuery({
        queryKey: ["library", id, "owns"],
        queryFn: () => hasEntryInLibrary(id!),
        enabled: !!id,
    });

    return {
        loading: cartLoading && libraryLoading,
        error: cartError && libraryError,
        inCart: cartData,
        inLibrary: libraryData,
    };
}
