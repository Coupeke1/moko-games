import Card from "@/components/cards/card";
import PriceIcon from "@/components/icons/price-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import { Height } from "@/components/layout/size";
import type { Entry } from "@/features/store/models/entry/entry.ts";

interface Props {
    entry: Entry;
}

export default function StoreCard({ entry }: Props) {
    return (
        <Card
            image={entry.image}
            title={entry.title}
            href={`/store/${entry.id}`}
            height={Height.ExtraLarge}
            information={
                <Row gap={Gap.Small} items={Items.Center} responsive={false}>
                    <PriceIcon />
                    <p>3h 40m</p>
                </Row>
            }
        />
    );
}
