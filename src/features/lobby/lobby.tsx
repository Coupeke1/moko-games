import BigButton from "@/components/buttons/big-button";
import InviteIcon from "@/components/icons/invite-icon";
import Column from "@/components/layout/column";
import {Gap} from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Section from "@/components/section";
import ErrorState from "@/components/state/error";
import State from "@/components/state/state";
import showToast from "@/components/toast";
import BotCard from "@/features/lobby/components/bot-card";
import GameInformation from "@/features/lobby/components/information";
import Page from "@/features/lobby/components/page";
import PlayerCard from "@/features/lobby/components/player-card";
import InviteDialog from "@/features/lobby/dialogs/invite-dialog/invite-dialog";
import SettingsDialog from "@/features/lobby/dialogs/settings-dialog/settings-dialog";
import {useSession} from "@/features/lobby/hooks/use-session";
import type {Player} from "@/features/lobby/models/player.ts";
import {closeLobby, startGame} from "@/features/lobby/services/lobby.ts";
import {useMutation} from "@tanstack/react-query";
import {useState} from "react";
import {useNavigate} from "react-router";

export default function LobbyPage() {
    const navigate = useNavigate();
    const [invite, setInvite] = useState(false);
    const [settings, setSettings] = useState(false);

    const {lobby, profile, game, isOwner, loading, error} = useSession();

    const start = useMutation({
        mutationFn: async ({lobby}: { lobby: string }) =>
            await startGame(lobby),
        onSuccess: async () => {
            showToast("Lobby", "Starting");
        },
        onError: (error: Error) => {
            showToast("Lobby", error.message);
        },
    });

    const close = useMutation({
        mutationFn: async ({lobby}: { lobby: string }) =>
            await closeLobby(lobby),
        onSuccess: async () => {
            showToast("Lobby", "Closed");
            navigate("/library");
        },
        onError: (error: Error) => {
            showToast("Lobby", error.message);
        },
    });

    return (
        <Page>
            <State
                data={profile && lobby && game}
                loading={loading}
                error={error}
            />

            {profile && lobby && game && (
                <>
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
                            isOwner={isOwner}
                            onStart={() => start.mutate({lobby: lobby.id})}
                            onQuit={() => close.mutate({lobby: lobby.id})}
                            onSettings={() => setSettings(true)}
                        />

                        <Section title="Players">
                            {lobby.players.length == 0 ? (
                                <ErrorState>No players</ErrorState>
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
                                        <BigButton
                                            onClick={() => setInvite(true)}
                                        >
                                            <InviteIcon/>
                                        </BigButton>
                                    )}
                                </Grid>
                            )}
                        </Section>
                    </Column>
                </>
            )}
        </Page>
    );
}
