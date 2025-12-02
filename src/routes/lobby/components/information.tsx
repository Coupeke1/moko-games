import CancelIcon from "@/components/icons/cancel-icon";
import PlayIcon from "@/components/icons/play-icon";
import SettingsIcon from "@/components/icons/settings-icon";
import { Gap } from "@/components/layout/gap";
import { Justify } from "@/components/layout/justify";
import Row from "@/components/layout/row";
import type { Game } from "@/models/game/game";

interface Props {
    game: Game;
    onStart: () => void;
    onQuit: () => void;
    onSettings: () => void;
}

export default function GameInformation({
    game,
    onStart,
    onQuit,
    onSettings,
}: Props) {
    return (
        <article
            className="flex flex-col justify-end relative overflow-hidden select-none bg-cover bg-center px-3 py-2 rounded-lg h-30"
            style={{ backgroundImage: `url("${game.image}")` }}
        >
            <section className="absolute inset-0 bg-linear-to-b from-black/20 via-black/60 to-black/80 from-0% via-45% to-100% rounded-lg" />

            <section className="relative z-10">
                <Row justify={Justify.Between} responsive={false}>
                    <h3 className="font-bold text-2xl">
                        {game.title.substring(0, 15)}
                        {game.title.length > 15 ? "..." : ""}
                    </h3>

                    <Row gap={Gap.Small} responsive={false}>
                        <button
                            onClick={onStart}
                            className="cursor-pointer text-fg-2 hover:text-fg transition-colors duration-75"
                        >
                            <PlayIcon />
                        </button>

                        <button
                            onClick={onQuit}
                            className="cursor-pointer text-fg-2 hover:text-fg transition-colors duration-75"
                        >
                            <CancelIcon big={true} />
                        </button>

                        <button
                            onClick={onSettings}
                            className="cursor-pointer text-fg-2 hover:text-fg transition-colors duration-75"
                        >
                            <SettingsIcon />
                        </button>
                    </Row>
                </Row>
            </section>
        </article>
    );
}
