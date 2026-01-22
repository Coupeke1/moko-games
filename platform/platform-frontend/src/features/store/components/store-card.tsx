import Card from "@/components/cards/card";
import PriceIcon from "@/components/icons/price-icon";
import TagIcon from "@/components/icons/tag-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import { Height } from "@/components/layout/size";
import { format } from "@/features/store/models/entry/category";
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
        />
    );
}
