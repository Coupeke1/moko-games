import Button from "@/components/buttons/button";
import Dialog from "@/components/dialog/dialog";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import TabContent from "@/components/tabs/buttons/content";
import TabRow from "@/components/tabs/buttons/row";
import showToast from "@/components/toast";
import type { Game } from "@/features/games/models/game.ts";
import type { Lobby } from "@/features/lobby/models/lobby.ts";
import GameTab from "@/features/lobby/dialogs/settings-dialog/game-tab";
import LobbyTab from "@/features/lobby/dialogs/settings-dialog/lobby-tab";
import { updateSettings } from "@/features/lobby/services/lobby.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";

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
    const [current, setCurrent] = useState<string>("Lobby");
    const [size, setSize] = useState<number>(4);

    useEffect(() => {
        setSize(lobby ? lobby.maxPlayers : 4);
    }, [lobby, open]);

    const save = useMutation({
        mutationFn: async ({ lobby, game }: { lobby: string; game: string }) =>
            await updateSettings(lobby, game, size),
        onSuccess: async (_data, variables) => {
            await client.invalidateQueries({
                queryKey: ["lobby", variables.lobby],
            });
            showToast("Lobby", "Saved");
            close();
        },
        onError: (error: Error) => {
            showToast("Lobby", error.message);
        },
    });

    return (
        <Dialog
            title="Settings"
            onClose={() => setCurrent("Lobby")}
            open={open}
            onChange={onChange}
            footer={
                <Button
                    fullWidth={true}
                    onClick={() =>
                        save.mutate({
                            lobby: lobby.id,
                            game: game.title,
                        })
                    }
                >
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
                            element: <GameTab />,
                        },
                    ]}
                />
            </Column>
        </Dialog>
    );
}
