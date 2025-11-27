import HeartIcon from "@/components/icons/heart-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import type { Game } from "@/models/library/game";

interface Props {
    game: Game;
}

export function Information({ game }: Props) {
    return (
        <Column gap={Gap.Large}>
            <article className="flex flex-col items-center justify-center relative overflow-hidden select-none bg-cover bg-center px-4 py-2 rounded-lg h-30" style={{ backgroundImage: `url("${game.image}")` }}>
                <section className="absolute inset-0 bg-black/30 rounded-lg" />

                <section className={`absolute right-2 top-2 cursor-pointer ${game.favourite ? "text-fg" : "text-fg-2"} hover:text-fg transition-colors duration-75`}>
                    <HeartIcon />
                </section>

                <section className="relative z-10">
                    <h3 className="font-bold text-2xl">{game.title.substring(0, 15)}{game.title.length > 15 ? "..." : ""}</h3>
                </section>
            </article>

            <p>{game.description}</p>
        </Column>
    );
}