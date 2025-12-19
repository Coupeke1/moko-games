import {useEffect, useState} from "react";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";

import Button from "@/components/buttons/button";
import Dialog from "@/components/dialog/dialog";
import Column from "@/components/layout/column";
import {Gap} from "@/components/layout/gap";
import TabContent from "@/components/tabs/buttons/content";
import TabRow from "@/components/tabs/buttons/row";
import showToast from "@/components/global/toast";
import type {Game} from "@/features/games/models/game.ts";
import type {Lobby} from "@/features/lobby/models/lobby.ts";
import LobbyTab from "@/features/lobby/dialogs/settings-dialog/lobby-tab";
import GameTab from "@/features/lobby/dialogs/settings-dialog/game-tab";
import {findLobbyGameSettings, updateSettings} from "@/features/lobby/services/lobby.ts";
import type {GameSettingsSchema} from "@/features/lobby/models/settings.ts";
import {findGameSettingsSchema} from "@/features/lobby/services/settings.ts";


interface Props {
    lobby: Lobby;
    game: Game;
    isOwner: boolean;
    close: () => void;
    open: boolean;
    onChange: (open: boolean) => void;
}

function defaultsFromSchema(schema: GameSettingsSchema): Record<string, unknown> {
    const out: Record<string, unknown> = {};
    for (const s of schema.settings) out[s.name] = s.defaultValue;
    return out;
}

export default function SettingsDialog({lobby, game, isOwner, close, open, onChange}: Props) {
    const qc = useQueryClient();
    const [current, setCurrent] = useState("Lobby");
    const [size, setSize] = useState<number>(4);
    const [gameSettings, setGameSettings] = useState<Record<string, unknown>>({});
    const [initialized, setInitialized] = useState(false);

    useEffect(() => {
        if (!open) {
            setInitialized(false);
            return;
        }
        setSize(lobby.maxPlayers);
    }, [open, lobby]);

    const schemaQuery = useQuery({
        queryKey: ["game-settings-schema", game.id],
        enabled: open && !!game?.id,
        queryFn: () => findGameSettingsSchema(game.id),
        staleTime: Infinity,
    });

    const lobbySettingsQuery = useQuery({
        queryKey: ["lobby-game-settings", lobby.id],
        enabled: open && !!lobby?.id,
        queryFn: () => findLobbyGameSettings(lobby.id),
        staleTime: 0,
    });

    useEffect(() => {
        if (!open || initialized) return;
        if (!lobbySettingsQuery.data) return;

        const fromLobby = lobbySettingsQuery.data ?? {};
        const merged =
            schemaQuery.data
                ? {...defaultsFromSchema(schemaQuery.data), ...fromLobby}
                : fromLobby;

        setGameSettings(merged);
        setInitialized(true);
    }, [open, initialized, lobbySettingsQuery.data, schemaQuery.data]);

    const setValue = (name: string, value: unknown) =>
        setGameSettings((prev) => ({...prev, [name]: value}));

    const save = useMutation({
        mutationFn: async () => updateSettings(lobby.id, size, gameSettings),
        onSuccess: async () => {
            await qc.invalidateQueries({queryKey: ["lobby", lobby.id]});
            await qc.invalidateQueries({queryKey: ["lobby-game-settings", lobby.id]});
            showToast("Lobby", "Saved");
            close();
        },
        onError: (error: Error) => showToast("Lobby", error.message),
    });

    return (
        <Dialog
            title="Settings"
            onClose={() => setCurrent("Lobby")}
            open={open}
            onChange={onChange}
            footer={
                <Button fullWidth onClick={() => save.mutate()}>
                    Save
                </Button>
            }
        >
            <Column gap={Gap.Large}>
                <TabRow tabs={["Lobby", "Game"]} current={current} setCurrent={setCurrent}/>

                <TabContent
                    current={current}
                    tabs={[
                        {
                            title: "Lobby",
                            element: (
                                <LobbyTab
                                    lobby={lobby}
                                    isOwner={isOwner}
                                    size={size.toString()}
                                    setSize={setSize}
                                />
                            ),
                        },
                        {
                            title: "Game",
                            element: (
                                <GameTab
                                    schema={schemaQuery.data ?? null}
                                    loading={schemaQuery.isLoading || lobbySettingsQuery.isLoading}
                                    isOwner={isOwner}
                                    values={gameSettings}
                                    setValue={setValue}
                                />
                            ),
                        },
                    ]}
                />
            </Column>
        </Dialog>
    );
}