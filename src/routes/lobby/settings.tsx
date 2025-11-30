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
import { isUserOwner } from "@/services/lobby-service";
import { useProfile } from "@/hooks/use-profile";

export default function LobbySettingsPage() {
    const navigate = useNavigate();
    const { id } = useParams();

    useEffect(() => {
        if (!id || id.length <= 0) navigate("/library");
    }, [id, navigate]);

    const {
        profile,
        isLoading: profileLoading,
        isError: profileError,
    } = useProfile();

    const {
        lobby,
        isLoading: lobbyLoading,
        isError: lobbyError,
    } = useLobby(id, profile?.id);

    if (lobbyLoading || profileLoading || !lobby || !profile)
        return (
            <Page>
                <LoadingState />
            </Page>
        );

    if (lobbyError || profileError)
        return (
            <Page>
                <ErrorState />
            </Page>
        );

    const isOwner: boolean = isUserOwner(profile.id, lobby);

    return (
        <Page>
            <Column gap={Gap.Large}>
                <GameInformation id={lobby.gameId} />
                <TabRow tabs={getTabs(lobby.id)} />

                <p>Settings {isOwner ? "owner" : "not owner"}</p>
            </Column>
        </Page>
    );
}
