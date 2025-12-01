import Column from "@/components/layout/column";
import type { Bot } from "@/models/profile/bot";
import type { ReactNode } from "react";
import Row from "@/components/layout/row";
import { Gap } from "@/components/layout/gap";
import BotIcon from "@/components/icons/bot-icon";
import { Items } from "@/components/layout/items";

interface Props {
    bot: Bot;
    footer: ReactNode;
}

export default function BotCard({ bot, footer }: Props) {
    return (
        <Column>
            <article
                className="flex flex-col relative overflow-hidden select-none justify-end bg-cover bg-center px-4 py-2 rounded-lg h-32"
                style={{ backgroundImage: `url("${bot.image}")` }}
            >
                <section className="absolute inset-0 bg-black/30 rounded-lg transition-colors duration-200" />

                <section className="relative z-10">
                    <h3 className="font-bold text-lg">
                        {bot.username.substring(0, 15)}
                        {bot.username.length > 15 ? "..." : ""}
                    </h3>

                    <Row gap={Gap.Large} responsive={false}>
                        <Row
                            gap={Gap.Small}
                            items={Items.Center}
                            responsive={false}
                        >
                            <BotIcon />
                            Beep Boop
                        </Row>
                    </Row>
                </section>
            </article>

            {footer}
        </Column>
    );
}
