import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import TabRow from "@/components/tabs/links/row";
import { useLobby } from "@/hooks/use-lobby";
import Page from "@/routes/lobby/components/page";
import { getTabs } from "@/routes/lobby/components/tabs";
import { useEffect } from "react";
import { useNavigate, useParams } from "react-router";
import GameInformation from "@/routes/lobby/components/information";

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