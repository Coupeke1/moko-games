import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Row from "@/components/layout/row";
import type { Player } from "@/models/lobby/player";
import type { Profile } from "@/models/profile/profile";
import type { ReactNode } from "react";

interface Props {
    user: Profile | Player;
    statistics?: ReactNode;
    footer: ReactNode;
}

export default function UserCard({ user, statistics, footer }: Props) {
    return (
        <Column>
            <article
                className="flex flex-col relative overflow-hidden select-none justify-end bg-cover bg-center px-4 py-2 rounded-lg h-32"
                style={{ backgroundImage: `url("${user.image}")` }}
            >
                <section className="absolute inset-0 bg-black/30 rounded-lg transition-colors duration-200" />

                <section className="relative z-10">
                    <h3 className="font-bold text-lg">
                        {user.username.substring(0, 15)}
                        {user.username.length > 15 ? "..." : ""}
                    </h3>

                    <Row gap={Gap.Large} responsive={false}>
                        {statistics && statistics}
                    </Row>
                </section>
            </article>

            {footer}
        </Column>
    );
}
