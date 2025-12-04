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
import PlayerCard from "@/routes/lobby/components/player-card";
import InviteDialog from "@/routes/lobby/dialogs/invite-dialog/invite-dialog";
import SettingsDialog from "@/routes/lobby/dialogs/settings-dialog/settings-dialog";
import { useSession } from "@/routes/lobby/hooks/use-session";
import { closeLobby, startGame } from "@/services/lobby/lobby-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import BotCard from "@/routes/lobby/components/bot-card";

export default function LobbyPage() {
    const client = useQueryClient();
    const navigate = useNavigate();
    const [invite, setInvite] = useState(false);
    const [settings, setSettings] = useState(false);

    const { lobby, profile, game, isOwner, isLoading, isError } = useSession();

    const start = useMutation({
        mutationFn: async ({ lobby }: { lobby: string }) =>
            await startGame(lobby),
        onSuccess: async (data, variables) => {
            await client.invalidateQueries({
                queryKey: ["lobby", variables.lobby],
            });

            if (!game) return;
            showToast("Lobby", "Starting...");

            window.location.replace(
                `${game.frontendUrl}${game.startEndpoint}/${data}`,
            );
        },
        onError: (error: Error) => {
            showToast("Lobby", error.message);
        },
    });

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
            <InviteDialog
                lobby={lobby}
                close={() => setInvite(false)}
                open={invite}
                onChange={setInvite}
            />
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
                    onStart={() => start.mutate({ lobby: lobby.id })}
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

                            {lobby.bot && (
                                <BotCard
                                    bot={lobby.bot}
                                    lobby={lobby}
                                    isOwner={isOwner}
                                />
                            )}

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
