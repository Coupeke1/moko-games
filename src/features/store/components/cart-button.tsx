import CartIcon from "@/components/icons/bar/cart-icon";
import showToast from "@/components/toast";
import { addToCart, removeFromCart } from "@/features/cart/services/cart";
import type { Entry } from "@/features/store/models/entry/entry";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useAvailable } from "@/features/store/hooks/use-available";
import AcceptIcon from "@/components/icons/accept-icon";

interface Props {
    entry: Entry;
}

export default function CartButton({ entry }: Props) {
    const client = useQueryClient();

    const { inCart, inLibrary, loading, error } = useAvailable(entry.id);

    const add = useMutation({
        mutationFn: async ({ entry }: { entry: Entry }) =>
            await addToCart(entry),
        onSuccess: async (_data, variables) => {
            await client.invalidateQueries({ queryKey: ["cart"] });
            showToast(variables.entry.title, "Added to cart");
        },
        onError: (error: Error, variables) =>
            showToast(variables.entry.title, error.message),
    });

    const remove = useMutation({
        mutationFn: async ({ entry }: { entry: Entry }) =>
            await removeFromCart(entry),
        onSuccess: async (_data, variables) => {
            await client.invalidateQueries({ queryKey: ["cart"] });
            showToast(variables.entry.title, "Removed from cart");
        },
        onError: (error: Error, variables) =>
            showToast(variables.entry.title, error.message),
    });

    if (inLibrary)
        return (
            <button disabled={true} className="text-fg">
                <CartIcon big={true} />
            </button>
        );

    return (
        <button
            onClick={
                inCart
                    ? () => remove.mutate({ entry })
                    : () => add.mutate({ entry })
            }
            disabled={loading || error}
            className={`cursor-pointer ${inCart ? "text-fg" : "text-fg-2 hover:text-fg"} transition-colors duration-75`}
        >
            <CartIcon big={true} />
        </button>
    );
}
