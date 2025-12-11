import Card from "@/components/cards/card";
import PriceIcon from "@/components/icons/price-icon";
import TagIcon from "@/components/icons/tag-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import { Height } from "@/components/layout/size";
import type { Entry } from "@/features/cart/models/entry.ts";
import { format } from "@/features/store/models/entry/category";

interface Props {
    entry: Entry;
}

export default function EntryLine({ entry }: Props) {
    return (
        <Card
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
            height={Height.Small}
        />
    );
}
