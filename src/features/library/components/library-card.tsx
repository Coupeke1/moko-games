import Card from "@/components/cards/card";
import HeartIcon from "@/components/icons/heart-icon";
import {Gap} from "@/components/layout/gap";
import {Items} from "@/components/layout/items";
import Row from "@/components/layout/row";
import {Height} from "@/components/layout/size";
import type {Entry} from "@/features/library/models/entry.ts";
import BoltIcon from "@/components/icons/bolt-icon.tsx";
import BoltSlashIcon from "@/components/icons/boltSlash-icon.tsx";

interface Props {
    entry: Entry;
}

export default function LibraryCard({entry}: Props) {
    return (
        <Card
            image={entry.image}
            title={entry.title}
            href={`/library/${entry.id}`}
            height={Height.ExtraLarge}
            information={
                <Row gap={Gap.Small} items={Items.Center} responsive={false}>
                    {entry.healthy ? <BoltIcon/> : <BoltSlashIcon/>}
                    {entry.healthy ? "Online" : "Offline"}
                </Row>
            }
            options={entry.favourite && <HeartIcon/>}
        />
    );
}
