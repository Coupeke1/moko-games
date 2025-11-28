import BigButton from "@/components/buttons/big-button";
import Button from "@/components/buttons/button";
import CancelIcon from "@/components/icons/cancel-icon";
import InviteIcon from "@/components/icons/invite-icon";
import StarIcon from "@/components/icons/star-icon";
import UsersIcon from "@/components/icons/users-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import Message from "@/components/state/message";
import TabRow from "@/components/tabs/links/row";
import showToast from "@/components/toast";
import { useLobby } from "@/hooks/use-lobby";
import { useProfile } from "@/hooks/use-profile";
import type { Lobby } from "@/models/lobby/lobby";
import type { Player } from "@/models/lobby/player";
import GameInformation from "@/routes/lobby/components/information";
import Page from "@/routes/lobby/components/page";
import PlayerCard from "@/routes/lobby/components/player-card";
import { getTabs } from "@/routes/lobby/components/tabs";
import InviteDialog from "@/routes/lobby/dialogs/invite-dialog";
import { isUserOwner, removePlayer } from "@/services/lobby-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";

function Player({ player, lobby, isOwner }: { player: Player; lobby: Lobby; isOwner: boolean }) {
    const client = useQueryClient();

    const remove = useMutation({
        mutationFn: async ({ player }: { player: Player }) => {
            await removePlayer(player.id, lobby.id)
        },
        onSuccess: async () => {
            await client.invalidateQueries({ queryKey: ["lobby", lobby.id] });
            showToast(player.username, "Removed");
        },
        onError: (error: Error) => {
            showToast(player.username, error.message);
        }
    });

    function handleRemove() {
        remove.mutate({ player });
    };

    const playerIsOwner: boolean = isUserOwner(player.id, lobby);

    return isOwner ? (
        playerIsOwner ? (
            <PlayerCard player={player} footer={
                <Button fullWidth={true} disabled={true}>
                    <StarIcon />
                </Button>
            } />
        ) : (
            <PlayerCard player={player} footer={
                <Button onClick={handleRemove} fullWidth={true}>
                    <CancelIcon />
                </Button>
            } />
        )
    ) : (
        playerIsOwner ? (
            <PlayerCard player={player} footer={
                <Button fullWidth={true} disabled={true}>
                    <StarIcon />
                </Button>
            } />
        ) : (
            <PlayerCard player={player} footer={
                <Button fullWidth={true} disabled={true}>
                    <UsersIcon />
                </Button>
            } />
        )
    )
}

export default function LobbyPlayersPage() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [invite, setInvite] = useState(false);

    useEffect(() => {
        if (!id || id.length <= 0) navigate("/library");
    }, [id]);

    const { profile, isLoading: profileLoading, isError: profileError } = useProfile();
    const { lobby, isLoading: lobbyLoading, isError: lobbyError } = useLobby(id!);

    if (lobbyLoading || profileLoading || !lobby || !profile)
        return <Page><LoadingState /></Page>;

    if (lobbyError || profileError)
        return <Page><ErrorState /></Page>;

    const isOwner: boolean = isUserOwner(profile.id, lobby);

    return (
        <Page>
            <InviteDialog lobby={lobby} open={invite} onChange={setInvite} />

            <Column gap={Gap.Large}>
                <GameInformation id={lobby.gameId} />
                <TabRow tabs={getTabs(lobby.id)} />

                {
                    lobby.players.length == 0 ? (
                        <Message>No players :(</Message>
                    ) : (
                        <Grid>
                            {
                                lobby.players.map((player: Player) => (
                                    <Player player={player} lobby={lobby} isOwner={isOwner} />
                                ))
                            }

                            {
                                isOwner && (
                                    <BigButton onClick={() => setInvite(true)}>
                                        <InviteIcon />
                                    </BigButton>
                                )
                            }
                        </Grid>
                    )
                }
            </Column>
        </Page>
    )
}