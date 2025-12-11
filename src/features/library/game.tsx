import HeartIcon from "@/components/icons/heart-icon";
import PlayIcon from "@/components/icons/play-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Justify } from "@/components/layout/justify";
import Page from "@/components/layout/page";
import Row from "@/components/layout/row";
import State from "@/components/state/state";
import showToast from "@/components/toast";
import { useFavourite } from "@/features/library/hooks/use-favourite.ts";
import { useGame } from "@/features/games/hooks/use-game";
import type { Game } from "@/features/games/models/game.ts";
import {
    favouriteEntry,
    unFavouriteEntry,
} from "@/features/library/services/library.ts";
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

    const { game, loading: gameLoading, error: gameError } = useGame(id);

    const {
        favourited,
        loading: favouritedLoading,
        error: favouritedError,
    } = useFavourite(id!);

    const favourite = useMutation({
        mutationFn: async ({
            favourited,
            game,
        }: {
            favourited: boolean;
            game: Game;
        }) => {
            if (favourited) {
                await unFavouriteEntry(game.id);
                return;
            }

            await favouriteEntry(game.id);
        },
        onSuccess: async (_data, variables) => {
            await client.refetchQueries({
                queryKey: ["library", "favourite", variables.game.id],
            });

            showToast(
                variables.game.title,
                variables.favourited ? "Unfavourited" : "Favourited",
            );
        },
        onError: (error: Error) => showToast("Favourite", error.message),
    });

    const start = useMutation({
        mutationFn: async ({ game }: { game: Game }) => {
            const lobby = await createLobby(game, 4);
            navigate(`/lobbies/${lobby.id}`);
        },
        onSuccess: async (_data, variables) => {
            await client.refetchQueries({ queryKey: ["lobby"] });
            showToast(variables.game.title, "Lobby was created");
            close();
        },
        onError: (error: Error) => showToast("Lobby", error.message),
    });

    return (
        <Page>
            <State
                data={game && favourited !== undefined}
                loading={gameLoading || favouritedLoading}
                error={gameError || favouritedError}
            />

            {game && favourited !== undefined && (
                <Column gap={Gap.Large}>
                    <article
                        className="flex flex-col justify-end relative overflow-hidden select-none bg-cover bg-center px-3 py-2 rounded-lg h-30"
                        style={{ backgroundImage: `url("${game.image}")` }}
                    >
                        <section className="absolute inset-0 bg-linear-to-b from-black/20 via-black/60 to-black/80 from-0% via-45% to-100% rounded-lg" />

                        <section className="relative z-10">
                            <Row justify={Justify.Between} responsive={false}>
                                <h3 className="font-bold text-2xl">
                                    {game.title.substring(0, 15)}
                                    {game.title.length > 15 ? "..." : ""}
                                </h3>

                                <Row gap={Gap.Small} responsive={false}>
                                    <button
                                        onClick={() =>
                                            favourite.mutate({
                                                favourited,
                                                game,
                                            })
                                        }
                                        className={`cursor-pointer ${favourited ? "text-fg" : "text-fg-2"} hover:text-fg transition-colors duration-75`}
                                    >
                                        <HeartIcon big={true} />
                                    </button>

                                    <button
                                        onClick={() => start.mutate({ game })}
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
        </Page>
    );
}
