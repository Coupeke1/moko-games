import Button from "@/components/buttons/button";
import Dialog from "@/components/dialog/dialog";
import HeartIcon from "@/components/icons/heart-icon";
import Column from "@/components/layout/column";
import showToast from "@/components/toast";
import type { Game } from "@/models/library/game";
import { favouriteGame, unFavouriteGame } from "@/services/library-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";

interface Props {
    game: Game;
    close: () => void;
    open: boolean;
    onChange: (open: boolean) => void;
}

export default function GameDialog({ game, close, open, onChange }: Props) {
    const client = useQueryClient();

    const [favourited, setFavourited] = useState(false);

    useEffect(() => {
        setFavourited(game.favourite);
    }, [game, open]);

    const favourite = useMutation({
        mutationFn: async ({ id, favourite }: { id: string, favourite: boolean; }) => {
            if (favourite) favouriteGame(id);
            else unFavouriteGame(id);
        },
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["library"] });

            showToast(game.title, "Game was favourited!");
            close();
        },
        onError: (error: Error) => {
            showToast(game.title, error.message);
        }
    });

    function handleFavourite() {
        favourite.mutate({ id: game.id, favourite: favourited });
    };

    return (
        <Dialog title="Game Details" open={open} onChange={onChange} footer={
            <Button>Start Lobby</Button>
        }>
            <Column>
                <article className="flex flex-col items-center justify-center relative overflow-hidden select-none bg-cover bg-center px-4 py-2 rounded-lg h-30" style={{ backgroundImage: `url("${game.image}")` }}>
                    <section className="absolute inset-0 bg-black/15 rounded-lg" />

                    <section onClick={handleFavourite} className={`absolute right-2 top-2 cursor-pointer ${game.favourite ? "text-fg" : "text-fg-2"} hover:text-fg transition-colors duration-75`}>
                        <HeartIcon />
                    </section>

                    <section className="relative z-10">
                        <h3 className="font-bold text-2xl">{game.title.substring(0, 15)}{game.title.length > 15 ? "..." : ""}</h3>
                    </section>
                </article>

                <p>{game.description}</p>
            </Column>
        </Dialog>
    );
}