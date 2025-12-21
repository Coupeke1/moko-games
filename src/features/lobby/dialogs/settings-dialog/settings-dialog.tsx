import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";

import Button from "@/components/buttons/button";
import Dialog from "@/components/dialog/dialog";
import showToast from "@/components/global/toast";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import TabContent from "@/components/tabs/buttons/content";
import TabRow from "@/components/tabs/buttons/row";
import type { Game } from "@/features/games/models/game";
import GameTab from "@/features/lobby/dialogs/settings-dialog/game-tab/game-tab";
import LobbyTab from "@/features/lobby/dialogs/settings-dialog/lobby-tab";
import type { Lobby } from "@/features/lobby/models/lobby.ts";
import { updateSettings } from "@/features/lobby/services/lobby.ts";

interface Props {
    lobby: Lobby;
    game: Game;
    isOwner: boolean;
    close: () => void;
    open: boolean;
    onChange: (open: boolean) => void;
}

export default function SettingsDialog({
    lobby,
    game,
    isOwner,
    close,
    open,
    onChange,
}: Props) {
    const client = useQueryClient();
    const [current, setCurrent] = useState("Lobby");
    const [size, setSize] = useState<number>(4);
    const [initialized, setInitialized] = useState(false);
    const [settings, setSettings] = useState<Record<string, unknown>>({});

    useEffect(() => {
        if (!open) {
            setInitialized(false);
            return;
        }
        setSize(lobby.maxPlayers);
    }, [open, lobby]);

    useEffect(() => {
        if (!open || initialized) return;
        setInitialized(true);
    }, [open, initialized]);

    const save = useMutation({
        mutationFn: async () => updateSettings(lobby.id, size, settings),
        onSuccess: async () => {
            await client.invalidateQueries({ queryKey: ["lobby", lobby.id] });
            await client.invalidateQueries({
                queryKey: ["lobby", lobby.id, "settings"],
            });
            showToast("Lobby", "Settings saved");
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
                <TabRow
                    tabs={["Lobby", "Game"]}
                    current={current}
                    setCurrent={setCurrent}
                />

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
                                    lobby={lobby}
                                    game={game}
                                    isOwner={isOwner}
                                    settings={settings}
                                    setSettings={setSettings}
                                    changeSetting={(
                                        name: string,
                                        value: unknown,
                                    ) => {
                                        setSettings((prev) => ({
                                            ...prev,
                                            [name]: value,
                                        }));
                                    }}
                                />
                            ),
                        },
                    ]}
                />
            </Column>
        </Dialog>
    );
}
