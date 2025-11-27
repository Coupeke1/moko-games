import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import { useGame } from "@/hooks/use-game";
import Page from "@/routes/lobby/components/page";

interface Props {
    id: string;
}

export default function GameInformation({ id }: Props) {
    const { game, isLoading, isError } = useGame(id);

    if (isLoading || game === undefined) return <Page><LoadingState /></Page>;
    if (isError) return <Page><ErrorState /></Page>;

    return (
        <article className="flex flex-col items-center justify-center relative overflow-hidden select-none bg-cover bg-center px-4 py-2 rounded-lg h-30" style={{ backgroundImage: `url("${game.imageUrl}")` }}>
            <section className="absolute inset-0 bg-black/30 rounded-lg" />

            <section className="relative z-10">
                <h3 className="font-bold text-2xl">{game.title.substring(0, 15)}{game.title.length > 15 ? "..." : ""}</h3>
            </section>
        </article>
    )
}
