import Card from "@/components/cards/card";
import ClockIcon from "@/components/icons/clock-icon";
import HeartIcon from "@/components/icons/heart-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import { Height } from "@/components/layout/size";
import type { Entry } from "@/models/library/entry";

interface Props {
    entry: Entry;
}

export default function LibraryCard({ entry }: Props) {
    return (
        <Card
            image={entry.image}
            title={entry.title}
            href={`/library/${entry.id}`}
            height={Height.ExtraLarge}
            information={
                <Row gap={Gap.None} items={Items.Center} responsive={false}>
                    <ClockIcon />
                    <p>3h 40m</p>
                </Row>
            }
            options={entry.favourite && <HeartIcon />}
        />
    );
}
