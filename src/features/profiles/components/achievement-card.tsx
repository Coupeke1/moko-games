import Card from "@/components/cards/card";
import CalendarIcon from "@/components/icons/calendar-icon";
import PuzzleIcon from "@/components/icons/puzzle-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";

interface Props {
    image: string;
    title: string;
    description?: string;
    date: string;
    game: string;
}

export default function AchievementCard({ image, title, date, game }: Props) {
    return (
        <Card
            title={title}
            image={image}
            information={
                <Row gap={Gap.Large} responsive={false}>
                    <Row
                        gap={Gap.Small}
                        items={Items.Center}
                        responsive={false}
                    >
                        <CalendarIcon />
                        <p>{date}</p>
                    </Row>

                    <Row
                        gap={Gap.Small}
                        items={Items.Center}
                        responsive={false}
                    >
                        <PuzzleIcon />
                        <p>{game}</p>
                    </Row>
                </Row>
            }
        />
    );
}
