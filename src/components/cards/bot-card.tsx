import Card from "@/components/cards/card";
import BotIcon from "@/components/icons/bot-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import type { Bot } from "@/features/profiles/models/bot.ts";
import type { ReactNode } from "react";

interface Props {
    bot: Bot;
    footer: ReactNode;
}

export default function BotCard({ bot, footer }: Props) {
    return (
        <Card
            image={bot.image}
            title={bot.username}
            information={
                <Row gap={Gap.Small} items={Items.Center} responsive={false}>
                    <BotIcon />
                    Beep Boop
                </Row>
            }
            footer={footer}
        />
    );
}
