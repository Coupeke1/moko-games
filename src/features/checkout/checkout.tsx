import Button from "@/components/buttons/button";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import { Justify } from "@/components/layout/justify";
import Page from "@/components/layout/page";
import Row from "@/components/layout/row";
import ErrorState from "@/components/state/error";
import State from "@/components/state/state";
import Statistic from "@/components/statistic";
import showToast from "@/components/toast";
import { useCart } from "@/features/cart/hooks/use-cart";
import type { Entry } from "@/features/cart/models/entry";
import { getTotalPrice } from "@/features/cart/services/cart";
import EntryLine from "@/features/checkout/components/entry-line";
import { placeOrder } from "@/features/checkout/services/checkout";
import { useMutation } from "@tanstack/react-query";
import { useEffect } from "react";
import { useNavigate } from "react-router";

export default function CheckoutPage() {
    const navigate = useNavigate();
    const { entries, loading, error } = useCart();

    useEffect(() => {
        if (entries && entries.length <= 0) navigate("/store");
    }, [entries, navigate]);

    const checkout = useMutation({
        mutationFn: async () => {
            showToast("Checkout", "Redirecting to payment");
            return await placeOrder();
        },
        onSuccess: async (data) => {
            window.location.replace(data);
        },
        onError: (error: Error) => showToast("Checkout", error.message),
    });

    return (
        <Page>
            <State data={entries} loading={loading} error={error} />
            <Column>
                <Row
                    items={Items.Stretch}
                    justify={Justify.Between}
                    responsive={false}
                >
                    <section className="w-36">
                        <Button
                            onClick={() => checkout.mutate()}
                            fullWidth={true}
                        >
                            Checkout
                        </Button>
                    </section>

                    {entries && (
                        <Row gap={Gap.Large} responsive={false}>
                            <Statistic
                                title="Amount"
                                value={entries.length.toString()}
                            />

                            <Statistic
                                title="Total Price"
                                value={`€${getTotalPrice(entries!)}`}
                            />
                        </Row>
                    )}
                </Row>

                {entries &&
                    (entries.length == 0 ? (
                        <ErrorState>No games in cart</ErrorState>
                    ) : (
                        <Column>
                            {entries.map((entry: Entry) => (
                                <EntryLine key={entry.id} entry={entry} />
                            ))}
                        </Column>
                    ))}
            </Column>
        </Page>
    );
}
