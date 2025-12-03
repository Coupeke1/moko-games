import Button from "@/components/buttons/button";
import Dialog from "@/components/dialog/dialog";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import TabContent from "@/components/tabs/buttons/content";
import TabRow from "@/components/tabs/buttons/row";
import showToast from "@/components/toast";
import type { Game } from "@/models/library/game";
import { Information } from "@/routes/library/dialogs/game-dialog/information";
import { Invites } from "@/routes/library/dialogs/game-dialog/invites";
import { createLobby } from "@/services/lobby/lobby-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { useNavigate } from "react-router";

interface Props {
    game: Game;
    close: () => void;
    open: boolean;
    onChange: (open: boolean) => void;
}

export default function GameDialog({ game, close, open, onChange }: Props) {
    const client = useQueryClient();
    const navigate = useNavigate();

    const [current, setCurrent] = useState<string>("Info");

    const startLobby = useMutation({
        mutationFn: async ({ game }: { game: Game }) => {
            const lobby = await createLobby(game, 4);
            navigate(`/lobby/${lobby.id}`);
        },
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["lobby"] });
            showToast(game.title, "Lobby was created");
            close();
        },
        onError: (error: Error) => {
            showToast(game.title, error.message);
        },
    });

    function handleStartLobby() {
        startLobby.mutate({ game });
    }

    return (
        <Dialog
            title="Game Details"
            onClose={() => setCurrent("Info")}
            open={open}
            onChange={onChange}
            footer={<Button onClick={handleStartLobby}>Start Lobby</Button>}
        >
            <Column gap={Gap.Large}>
                <TabRow
                    tabs={["Info", "Invites"]}
                    current={current}
                    setCurrent={setCurrent}
                />

                <TabContent
                    current={current}
                    tabs={[
                        {
                            title: "Info",
                            element: <Information game={game} />,
                        },
                        {
                            title: "Invites",
                            element: <Invites game={game} />,
                        },
                    ]}
                />
            </Column>
        </Dialog>
    );
}
