import Button from "@/components/buttons/button";
import Dialog from "@/components/dialog/dialog";
import Grid from "@/components/layout/grid/grid";
import ErrorState from "@/components/state/error";
import State from "@/components/state/state";
import { useCart } from "@/features/cart/hooks/use-cart.ts";
import type { Entry } from "@/features/cart/models/entry.ts";
import EntryCard from "@/features/cart/components/entry-card";
import { getTotalPrice } from "@/features/cart/services/cart.ts";

interface Props {
    open: boolean;
    onChange: (open: boolean) => void;
}

export default function CartDialog({ open, onChange }: Props) {
    const { entries, loading, error } = useCart(open);

    return (
        <Dialog
            title="Cart"
            open={open}
            onChange={onChange}
            footer={
                open && entries ? (
                    <Button>Checkout (€{getTotalPrice(entries)})</Button>
                ) : (
                    <Button>Checkout (€0)</Button>
                )
            }
        >
            <State data={entries} loading={loading} error={error} />

            {open &&
                entries &&
                (entries.length == 0 ? (
                    <ErrorState>No games in cart</ErrorState>
                ) : (
                    <Grid>
                        {entries.map((entry: Entry) => (
                            <EntryCard key={entry.id} entry={entry} />
                        ))}
                    </Grid>
                ))}
        </Dialog>
    );
}
