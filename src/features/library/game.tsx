import PlayIcon from "@/components/icons/play-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Justify } from "@/components/layout/justify";
import Page from "@/components/layout/page";
import Row from "@/components/layout/row";
import State from "@/components/state/state";
import showToast from "@/components/toast";
import { useGame } from "@/features/games/hooks/use-game";
import type { Game } from "@/features/games/models/game.ts";
import Favourite from "@/features/library/components/favourite";
import { createLobby } from "@/features/lobby/services/lobby.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect } from "react";
import { useNavigate, useParams } from "react-router";

export default function LibraryGamePage() {
    const client = useQueryClient();
    const navigate = useNavigate();
    const params = useParams();
    const id = params.id;

    useEffect(() => {
        if (!id) navigate("/library");
    }, [id, navigate]);

    const { game, loading, error } = useGame(id);

    const start = useMutation({
        mutationFn: async ({ game }: { game: Game }) => {
            showToast(game.title, "Creating lobby");
            const lobby = await createLobby(game, 4);
            navigate(`/lobbies/${lobby.id}`);
        },
        onSuccess: async (_data, variables) => {
            showToast(variables.game.title, "Lobby was created");
            await client.refetchQueries({ queryKey: ["lobby"] });
        },
        onError: (error: Error) => showToast("Lobby", error.message),
    });

    return (
        <Page>
            <State
                loading={loading}
                error={error}
                empty={!game}
                message="Game not found"
            >
                {game && (
                    <Column gap={Gap.Large}>
                        <article
                            className="flex flex-col justify-end relative overflow-hidden select-none bg-cover bg-center px-3 py-2 rounded-lg h-30"
                            style={{ backgroundImage: `url("${game.image}")` }}
                        >
                            <section className="absolute inset-0 bg-linear-to-b from-black/20 via-black/60 to-black/80 from-0% via-45% to-100% rounded-lg" />

                            <section className="relative z-10">
                                <Row
                                    justify={Justify.Between}
                                    responsive={false}
                                >
                                    <h3 className="font-bold text-2xl truncate">
                                        {game.title}
                                    </h3>

                                    <Row gap={Gap.Small} responsive={false}>
                                        <Favourite game={game} />

                                        <button
                                            onClick={() =>
                                                start.mutate({ game })
                                            }
                                            className="cursor-pointer text-fg-2 hover:text-fg transition-colors duration-75"
                                        >
                                            <PlayIcon />
                                        </button>
                                    </Row>
                                </Row>
                            </section>
                        </article>

                        <p className="text-fg-2">{game.description}</p>

                        <p>TODO: Show achievements for {game.title} here</p>
                    </Column>
                )}
            </State>
        </Page>
    );
}
