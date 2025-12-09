import Card from "@/components/cards/card";
import PriceIcon from "@/components/icons/price-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import type { Entry } from "@/models/store/entry";

interface Props {
    entry: Entry;
}

export default function CartCard({ entry }: Props) {
    <Card
        key={entry.id}
        title={entry.title}
        image={entry.image}
        information={
            <Row gap={Gap.Small} items={Items.Center} responsive={false}>
                <PriceIcon />
                <p>{entry.price}</p>
            </Row>
        }
    />;
}
