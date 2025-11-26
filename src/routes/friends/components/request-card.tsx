import ClockIcon from "@/components/icons/clock-icon";
import LevelIcon from "@/components/icons/level-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import type { Profile } from "@/models/profile/profile";
import type { ReactNode } from "react";

interface Props {
    request: Profile;
    footer: ReactNode;
}

export default function IncomingRequestCard({ request, footer }: Props) {
    return (
        <Column>
            <article className="flex flex-col relative overflow-hidden select-none justify-end bg-cover bg-center px-4 py-2 rounded-lg h-32" style={{ backgroundImage: `url("${request.image}")` }}>
                <section className="absolute inset-0 bg-black/30 rounded-lg transition-colors duration-200" />

                <section className="relative z-10">
                    <h3 className="font-bold text-lg">{request.username.substring(0, 15)}{request.username.length > 15 ? "..." : ""}</h3>
                    <Row gap={Gap.Large} responsive={false}>
                        <Row gap={Gap.None} items={Items.Center} responsive={false}>
                            <LevelIcon />
                            <p>{request.statistics.level}</p>
                        </Row>

                        <Row gap={Gap.None} items={Items.Center} responsive={false}>
                            <ClockIcon />
                            <p>{request.statistics.playTime}</p>
                        </Row>
                    </Row>
                </section>
            </article>

            {footer}
        </Column>
    )
}