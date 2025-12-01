import BigButton from "@/components/buttons/big-button";
import InviteIcon from "@/components/icons/invite-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Section from "@/components/section";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import Message from "@/components/state/message";
import showToast from "@/components/toast";
import type { Player } from "@/models/lobby/player";
import GameInformation from "@/routes/lobby/components/information";
import Page from "@/routes/lobby/components/page";
import PlayerCard from "@/routes/lobby/components/player";
import InviteDialog from "@/routes/lobby/dialogs/invite-dialog/invite-dialog";
import SettingsDialog from "@/routes/lobby/dialogs/settings-dialog/settings-dialog";
import { useLobbyData } from "@/routes/lobby/hooks/use-lobby";
import { closeLobby } from "@/services/lobby-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";

export default function LobbyPage() {
    const client = useQueryClient();
    const navigate = useNavigate();
    const { id } = useParams();
    const [invite, setInvite] = useState(false);
    const [settings, setSettings] = useState(false);

    useEffect(() => {
        if (!id || id.length <= 0) navigate("/library");
    }, [id, navigate]);

    const { lobby, profile, game, isOwner, isLoading, isError } =
        useLobbyData();

    const close = useMutation({
        mutationFn: async ({ lobby }: { lobby: string }) =>
            await closeLobby(lobby),
        onSuccess: async (_data, variables) => {
            await client.invalidateQueries({
                queryKey: ["lobby", variables.lobby],
            });
            showToast("Lobby", "Closed");
            navigate("/library");
        },
        onError: (error: Error) => {
            showToast("Lobby", error.message);
        },
    });

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
            <SettingsDialog
                lobby={lobby}
                game={game}
                isOwner={isOwner}
                close={() => setSettings(false)}
                open={settings}
                onChange={setSettings}
            />

            <Column gap={Gap.Large}>
                <GameInformation
                    game={game}
                    onStart={() => {}}
                    onQuit={() => close.mutate({ lobby: lobby.id })}
                    onSettings={() => setSettings(true)}
                />

                <Section title="Players">
                    {lobby.players.length == 0 ? (
                        <Message>No players :(</Message>
                    ) : (
                        <Grid>
                            {lobby.players.map((player: Player) => (
                                <PlayerCard
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
