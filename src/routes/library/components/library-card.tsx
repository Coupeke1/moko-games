import Card from "@/components/cards/card";
import ClockIcon from "@/components/icons/clock-icon";
import HeartIcon from "@/components/icons/heart-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import { Size } from "@/components/layout/size";
import type { Game } from "@/models/library/game";

interface Props {
    game: Game;
}

export default function LibraryCard({ game }: Props) {
    return (
        <Card
            image={game.image}
            title={game.title}
            href={`/library/${game.id}`}
            height={Size.ExtraLarge}
            information={
                <Row gap={Gap.None} items={Items.Center} responsive={false}>
                    <ClockIcon />
                    <p>3h 40m</p>
                </Row>
            }
            options={game.favourite && <HeartIcon />}
        />
    );
}
