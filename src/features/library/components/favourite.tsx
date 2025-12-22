import show from "@/components/global/toast/toast";
import { Type } from "@/components/global/toast/type";
import HeartIcon from "@/components/icons/heart-icon";
import type { Game } from "@/features/games/models/game";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useFavourite } from "../hooks/use-favourite";
import { favouriteEntry, unFavouriteEntry } from "../services/library";

interface Props {
    game: Game;
}

export default function Favourite({ game }: Props) {
    const client = useQueryClient();
    const { favourited, loading, error } = useFavourite(game.id);

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
        },
        onError: (error: Error) => show(Type.Library, error.message),
    });

    if (favourited === null || loading || error) return null;

    return (
        <button
            onClick={() => favourite.mutate({ favourited, game })}
            className={`cursor-pointer ${favourited ? "text-fg" : "text-fg-2"} hover:text-fg transition-colors duration-75`}
        >
            <HeartIcon big={true} />
        </button>
    );
}
