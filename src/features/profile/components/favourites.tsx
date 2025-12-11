import Section from "@/components/section";
import State from "@/components/state/state";
import Column from "@/components/layout/column";
import {useFavourites} from "@/features/profile/hooks/use-favourites";
import FavouriteCard from "@/features/profile/components/favourite-card";
import {Gap} from "@/components/layout/gap";

interface Props {
    enabled: boolean;
}

export default function FavouritesSection({enabled}: Props) {
    const {favourites, loading, error} = useFavourites();

    if (!enabled) return null;

    return (
        <Section title="Favourites">
            <State data={favourites} loading={loading} error={error}/>

            {favourites && favourites.games.length > 0 && (
                <Column gap={Gap.Large}>
                    {favourites.games.map((game) => (
                        <FavouriteCard
                            key={game.id}
                            image={game.image}
                            title={game.title}
                            description={game.description}
                            date={new Date(game.purchasedAt).toLocaleDateString()}
                        />
                    ))}
                </Column>
            )}

            {favourites && favourites.games.length === 0 && (
                <p className="text-center text-gray-500 py-4">
                    No favourite games yet. Mark games as favourite in your library!
                </p>
            )}
        </Section>
    );
}