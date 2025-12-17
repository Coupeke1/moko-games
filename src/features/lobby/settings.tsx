import Button from "@/components/buttons/button";
import CancelIcon from "@/components/icons/cancel-icon";
import PlayIcon from "@/components/icons/play-icon";
import Input from "@/components/inputs/input";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import { GridSize } from "@/components/layout/grid/size";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import TabRow from "@/components/tabs/links/row";
import Page from "@/features/lobby/components/page";
import { getTabs } from "@/features/lobby/components/tabs";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { useSession } from "@/features/lobby/hooks/use-session";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import showToast from "@/components/global/toast";
import {
    allPlayersReady,
    updateSettings,
} from "@/features/lobby/services/lobby.ts";

export default function LobbySettingsPage() {
    const client = useQueryClient();
    const navigate = useNavigate();
    const { id } = useParams();

    useEffect(() => {
        if (!id || id.length <= 0) navigate("/library");
    }, [id, navigate]);

    const [size, setSize] = useState<number>(4);

    const {
        lobby,
        profile,
        game,
        isOwner,
        loading: isLoading,
        error: isError,
    } = useSession();

    const save = useMutation({
        mutationFn: async ({ lobby, game }: { lobby: string; game: string }) =>
            await updateSettings(lobby, game, size),
        onSuccess: async (_data, variables) => {
            await client.invalidateQueries({
                queryKey: ["lobby", variables.lobby],
            });
            showToast("Lobby", "Saved");
        },
        onError: (error: Error) => {
            showToast("Lobby", error.message);
        },
    });

    useEffect(() => {
        if (!lobby) return;
        setSize(lobby.maxPlayers);
    }, [lobby]);

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

    const ready: boolean = allPlayersReady(lobby);

    return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow tabs={getTabs(lobby.id)} />

                {isOwner && (
                    <Grid size={GridSize.Small}>
                        <Button disabled={!ready} fullWidth={true}>
                            <PlayIcon />
                        </Button>

                        <Button fullWidth={true}>
                            <CancelIcon />
                        </Button>
                    </Grid>
                )}

                <Input
                    label="Max Players"
                    disabled={!isOwner}
                    type="number"
                    value={size.toString()}
                    onChange={(e) => setSize(Number(e.target.value))}
                />

                {isOwner && (
                    <Button
                        onClick={() =>
                            save.mutate({ lobby: lobby.id, game: game.title })
                        }
                    >
                        Save
                    </Button>
                )}
            </Column>
        </Page>
    );
}
