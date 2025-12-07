import Card from "@/components/cards/card";
import ClockIcon from "@/components/icons/clock-icon";
import HeartIcon from "@/components/icons/heart-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import { Size } from "@/components/layout/size";
import type { Entry } from "@/models/store/entry";

interface Props {
    entry: Entry;
}

export default function StoreCard({ entry }: Props) {
    return (
        <Card
            image={entry.image}
            title={entry.title}
            href={`/store/${entry.id}`}
            height={Size.ExtraLarge}
            information={
                <Row gap={Gap.None} items={Items.Center} responsive={false}>
                    <ClockIcon />
                    <p>3h 40m</p>
                </Row>
            }
        />
    );
}
