import Button from "@/components/buttons/button";
import Dialog from "@/components/dialog/dialog";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import type { Game } from "@/models/library/game";

interface Props {
    game: Game;
    close: () => void;
    open: boolean;
    onChange: (open: boolean) => void;
}

export default function GameDialog({ game, close, open, onChange }: Props) {
    return (
        <Dialog title={game.title} open={open} onChange={onChange} footer={
            <Button>Play</Button>
        }>
            <Column gap={Gap.Large}>
                <article className="flex flex-col items-center justify-center relative overflow-hidden select-none bg-cover bg-center px-4 py-2 rounded-lg h-30" style={{ backgroundImage: `url("${game.image}")` }}>
                    <section className="absolute inset-0 bg-black/15 rounded-lg" />

                    <section className="relative z-10">
                        <h3 className="font-bold text-2xl">{game.title.substring(0, 15)}{game.title.length > 15 ? "..." : ""}</h3>
                    </section>
                </article>

                <p>{game.description}</p>
            </Column>
        </Dialog>
    );
}