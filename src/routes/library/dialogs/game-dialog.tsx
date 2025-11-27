import Button from "@/components/buttons/button";
import Dialog from "@/components/dialog/dialog";
import HeartIcon from "@/components/icons/heart-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import TabContent from "@/components/tabs/content";
import TabRow from "@/components/tabs/row";
import showToast from "@/components/toast";
import type { Game } from "@/models/library/game";
import GameSettings from "@/routes/library/components/game-settings";
import LobbySettings from "@/routes/library/components/lobby-settings";
import { createLobby } from "@/services/lobby-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
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
    const [size, setSize] = useState<number>(2);

    const startLobby = useMutation({
        mutationFn: async ({ game, size }: { game: Game; size: number; }) => {
            const lobby = await createLobby(game, size);
            navigate(`/lobby/${lobby.id}`);
        },
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["lobby"] });

            showToast(game.title, "Lobby was created");
            close();
        },
        onError: (error: Error) => {
            showToast(game.title, error.message);
        }
    });

    function handleStartLobby() {
        startLobby.mutate({ game, size });
    };

    return (
        <Dialog title="Game Details" onClose={() => setCurrent("Info")} open={open} onChange={onChange} footer={
            <Button onClick={handleStartLobby}>Start Lobby</Button>
        }>
            <Column gap={Gap.Large}>
                <TabRow
                    tabs={["Info", "Settings"]}
                    current={current}
                    setCurrent={setCurrent}
                />

                <TabContent
                    current={current}
                    tabs={[{
                        title: "Info", element: (
                            <Column gap={Gap.Large}>
                                <article className="flex flex-col items-center justify-center relative overflow-hidden select-none bg-cover bg-center px-4 py-2 rounded-lg h-30" style={{ backgroundImage: `url("${game.image}")` }}>
                                    <section className="absolute inset-0 bg-black/15 rounded-lg" />

                                    <section className={`absolute right-2 top-2 cursor-pointer ${game.favourite ? "text-fg" : "text-fg-2"} hover:text-fg transition-colors duration-75`}>
                                        <HeartIcon />
                                    </section>

                                    <section className="relative z-10">
                                        <h3 className="font-bold text-2xl">{game.title.substring(0, 15)}{game.title.length > 15 ? "..." : ""}</h3>
                                    </section>
                                </article>

                                <p>{game.description}</p>
                            </Column>
                        )
                    },
                    {
                        title: "Settings", element: (
                            <Column>
                                <LobbySettings size={size} setSize={setSize} />
                                <GameSettings game={game} />
                            </Column>
                        )
                    }]}
                />
            </Column>
        </Dialog>
    );
}