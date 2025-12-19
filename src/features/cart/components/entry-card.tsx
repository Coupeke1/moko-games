import Card from "@/components/cards/card";
import PriceIcon from "@/components/icons/price-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import type { Entry } from "@/features/cart/models/entry.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { removeFromCart } from "@/features/cart/services/cart";
import showToast from "@/components/global/toast";
import Button from "@/components/buttons/button";
import CancelIcon from "@/components/icons/cancel-icon";
import TagIcon from "@/components/icons/tag-icon";
import { format } from "@/features/store/models/entry/category";

interface Props {
    entry: Entry;
}

export default function EntryCard({ entry }: Props) {
    const client = useQueryClient();

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

    return (
        <Card
            key={entry.id}
            title={entry.title}
            image={entry.image}
            information={
                <Row responsive={false}>
                    <Row
                        gap={Gap.Small}
                        items={Items.Center}
                        responsive={false}
                    >
                        <PriceIcon />
                        <p>{entry.price}</p>
                    </Row>
                    <Row
                        gap={Gap.Small}
                        items={Items.Center}
                        responsive={false}
                    >
                        <TagIcon />
                        <p>{format(entry.category)}</p>
                    </Row>
                </Row>
            }
            footer={
                <Button
                    onClick={() => remove.mutate({ entry })}
                    fullWidth={true}
                >
                    <CancelIcon />
                </Button>
            }
        />
    );
}
