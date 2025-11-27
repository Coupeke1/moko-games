import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import TabRow from "@/components/tabs/links/row";
import { useGame } from "@/hooks/use-game";
import { useLobby } from "@/hooks/use-lobby";
import Page from "@/routes/lobby/components/page";
import { getTabs } from "@/routes/lobby/components/tabs";
import { useEffect } from "react";
import { useNavigate, useParams } from "react-router";

function GameInformation({ id }: { id: string }) {
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

export default function LobbySettingsPage() {
    const navigate = useNavigate();
    const { id } = useParams();

    useEffect(() => {
        if (!id || id.length <= 0) navigate("/library");
    }, [id]);

    const { lobby, isLoading, isError } = useLobby(id!);

    if (isLoading || lobby === undefined) return <Page><LoadingState /></Page>;
    if (isError) return <Page><ErrorState /></Page>;

    return (
        <Page>
            <Column gap={Gap.Large}>
                <GameInformation id={lobby.gameId} />
                <TabRow tabs={getTabs(lobby.id)} />

                <p>Settings</p>
            </Column>
        </Page>
    )
}