import Column from "@/components/layout/column";
import type { Game } from "@/models/library/game";
import type { Lobby } from "@/models/lobby/lobby";
import type { Player } from "@/models/lobby/player";
import { findOwner } from "@/services/lobby-service";
import type { ReactNode } from "react";

interface Props {
    lobby: Lobby;
    game: Game;
    footer: ReactNode;
}

export default function InviteCard({ lobby, game, footer }: Props) {
    const owner: Player = findOwner(lobby);

    return (
        <Column>
            <article className="flex flex-col relative overflow-hidden select-none justify-end bg-cover bg-center px-4 py-2 rounded-lg h-32" style={{ backgroundImage: `url("${owner.image}")` }}>
                <section className="absolute inset-0 bg-black/30 rounded-lg transition-colors duration-200" />

                <section className="relative z-10">
                    <h3 className="font-bold text-lg">{owner.username.substring(0, 15)}{owner.username.length > 15 ? "..." : ""}</h3>
                </section>
            </article>

            {footer}
        </Column>
    )
}