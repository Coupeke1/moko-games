import AcceptIcon from "@/components/icons/accept-icon";
import RejectIcon from "@/components/icons/reject-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import type { Player } from "@/models/lobby/player";
import type { ReactNode } from "react";

interface Props {
    player: Player;
    footer: ReactNode;
}

export default function PlayerCard({ player, footer }: Props) {
    return (
        <Column>
            <article
                className="flex flex-col relative overflow-hidden select-none justify-end bg-cover bg-center px-4 py-2 rounded-lg h-32"
                style={{ backgroundImage: `url("${player.image}")` }}
            >
                <section className="absolute inset-0 bg-black/30 rounded-lg transition-colors duration-200" />

                <section className="relative z-10">
                    <h3 className="font-bold text-lg">
                        {player.username.substring(0, 15)}
                        {player.username.length > 15 ? "..." : ""}
                    </h3>
                    <Row gap={Gap.Large} responsive={false}>
                        <Row
                            gap={Gap.Small}
                            items={Items.Center}
                            responsive={false}
                        >
                            {player.ready ? <AcceptIcon /> : <RejectIcon />}
                            {player.ready ? "Ready" : "Not Ready"}
                        </Row>
                    </Row>
                </section>
            </article>

            {footer}
        </Column>
    );
}
