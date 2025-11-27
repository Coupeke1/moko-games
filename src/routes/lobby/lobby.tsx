import InviteIcon from "@/components/icons/invite-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import TabRow from "@/components/tabs/links/row";
import { useLobby } from "@/hooks/use-lobby";
import GameInformation from "@/routes/lobby/components/information";
import Page from "@/routes/lobby/components/page";
import { getTabs } from "@/routes/lobby/components/tabs";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import InviteDialog from "@/routes/lobby/dialogs/invite-dialog";

export default function LobbyPage() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [invite, setInvite] = useState(false);

    useEffect(() => {
        if (!id || id.length <= 0) navigate("/library");
    }, [id]);

    const { lobby, isLoading, isError } = useLobby(id!);

    if (isLoading || lobby === undefined) return <Page><LoadingState /></Page>;
    if (isError) return <Page><ErrorState /></Page>;

    return (
        <Page>
            <InviteDialog lobby={lobby} open={invite} onChange={setInvite} />

            <Column gap={Gap.Large}>
                <GameInformation id={lobby.gameId} />
                <TabRow tabs={getTabs(lobby.id)} />

                <Grid>
                    <button onClick={() => setInvite(true)} className="bg-bg-2 hover:bg-bg-3 transition-colors duration-75 flex flex-col justify-center items-center cursor-pointer select-none x-4 py-2 rounded-lg h-48">
                        <InviteIcon />
                    </button>
                </Grid>
            </Column>
        </Page>
    )
}