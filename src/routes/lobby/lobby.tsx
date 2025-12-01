import BigButton from "@/components/buttons/big-button";
import Button from "@/components/buttons/button";
import UserCard from "@/components/cards/user-card";
import AcceptIcon from "@/components/icons/accept-icon";
import CancelIcon from "@/components/icons/cancel-icon";
import InviteIcon from "@/components/icons/invite-icon";
import RejectIcon from "@/components/icons/reject-icon";
import StarIcon from "@/components/icons/star-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import Section from "@/components/section";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import Message from "@/components/state/message";
import showToast from "@/components/toast";
import type { Lobby } from "@/models/lobby/lobby";
import type { Player } from "@/models/lobby/player";
import type { Profile } from "@/models/profile/profile";
import GameInformation from "@/routes/lobby/components/information";
import Page from "@/routes/lobby/components/page";
import InviteDialog from "@/routes/lobby/dialogs/invite-dialog";
import { useLobbyData } from "@/routes/lobby/hooks/use-lobby";
import {
    isUserOwner,
    readyPlayer,
    removePlayer,
    unReadyPlayer,
} from "@/services/lobby-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";

function Player({
    player,
    lobby,
    profile,
    isOwner,
}: {
    player: Player;
    lobby: Lobby;
    profile: Profile;
    isOwner: boolean;
}) {
    const client = useQueryClient();

    const remove = useMutation({
        mutationFn: async () => await removePlayer(player.id, lobby.id),
        onSuccess: async () => {
            await client.invalidateQueries({ queryKey: ["lobby", lobby.id] });
            showToast(player.username, "Removed");
        },
        onError: (error: Error) => showToast(player.username, error.message),
    });

    const ready = useMutation({
        mutationFn: async () => await readyPlayer(lobby.id),
        onSuccess: async () => {
            await client.invalidateQueries({ queryKey: ["lobby", lobby.id] });
            showToast(player.username, "Ready");
        },
        onError: (error: Error) => showToast(player.username, error.message),
    });

    const unReady = useMutation({
        mutationFn: async () => await unReadyPlayer(lobby.id),
        onSuccess: async () => {
            await client.invalidateQueries({ queryKey: ["lobby", lobby.id] });
            showToast(player.username, "Not Ready");
        },
        onError: (error: Error) => showToast(player.username, error.message),
    });

    function PlayerButton() {
        const isCurrentPlayer: boolean = player.id === profile.id;
        const playerIsOwner: boolean = isUserOwner(player.id, lobby);

        if (isCurrentPlayer) {
            return (
                <Button
                    onClick={player.ready ? unReady.mutate : ready.mutate}
                    fullWidth={true}
                >
                    {player.ready ? <AcceptIcon /> : <RejectIcon />}
                </Button>
            );
        }

        if (isOwner && !playerIsOwner) {
            return (
                <Button onClick={remove.mutate} fullWidth={true}>
                    <CancelIcon />
                </Button>
            );
        }

        if (playerIsOwner) {
            return (
                <Button disabled={true} fullWidth={true}>
                    <StarIcon />
                </Button>
            );
        }

        return (
            <Button disabled={true} fullWidth={true}>
                {player.ready ? <AcceptIcon /> : <RejectIcon />}
            </Button>
        );
    }

    return (
        <UserCard
            user={player}
            statistics={
                <Row gap={Gap.Small} items={Items.Center} responsive={false}>
                    {player.ready ? <AcceptIcon /> : <RejectIcon />}
                    {player.ready ? "Ready" : "Not Ready"}
                </Row>
            }
            footer={PlayerButton()}
        />
    );
}

export default function LobbyPage() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [invite, setInvite] = useState(false);

    useEffect(() => {
        if (!id || id.length <= 0) navigate("/library");
    }, [id, navigate]);

    const { lobby, profile, game, isOwner, isLoading, isError } =
        useLobbyData();

    if (isLoading || !lobby || !profile || !game)
        return (
            <Page>
                <LoadingState />
            </Page>
        );

    if (isError)
        return (
            <Page>
                <ErrorState />
            </Page>
        );

    return (
        <Page>
            <InviteDialog lobby={lobby} open={invite} onChange={setInvite} />
            <Column gap={Gap.Large}>
                <GameInformation game={game} />

                <Section title="Players">
                    {lobby.players.length == 0 ? (
                        <Message>No players :(</Message>
                    ) : (
                        <Grid>
                            {lobby.players.map((player: Player) => (
                                <Player
                                    key={player.id}
                                    player={player}
                                    lobby={lobby}
                                    profile={profile}
                                    isOwner={isOwner}
                                />
                            ))}

                            {isOwner && (
                                <BigButton onClick={() => setInvite(true)}>
                                    <InviteIcon />
                                </BigButton>
                            )}
                        </Grid>
                    )}
                </Section>
            </Column>
        </Page>
    );
}
