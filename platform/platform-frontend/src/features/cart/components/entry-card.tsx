import Button from "@/components/buttons/button";
import Card from "@/components/cards/card";
import show from "@/components/global/toast/toast";
import { Type } from "@/components/global/toast/type";
import CancelIcon from "@/components/icons/cancel-icon";
import PriceIcon from "@/components/icons/price-icon";
import TagIcon from "@/components/icons/tag-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import type { Entry } from "@/features/cart/models/entry.ts";
import { removeFromCart } from "@/features/cart/services/cart";
import { format } from "@/features/store/models/entry/category";
import { useMutation, useQueryClient } from "@tanstack/react-query";

interface Props {
    entry: Entry;
}

export default function EntryCard({ entry }: Props) {
    const client = useQueryClient();

    const remove = useMutation({
        mutationFn: async ({ entry }: { entry: Entry }) =>
            await removeFromCart(entry),
        onSuccess: async () =>
            await client.invalidateQueries({ queryKey: ["cart"] }),
        onError: (error: Error) => show(Type.Cart, error.message),
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
