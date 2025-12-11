import Section from "@/components/section";
import State from "@/components/state/state";
import Column from "@/components/layout/column";
import { useFavourites } from "@/features/profile/hooks/use-favourites";
import FavouriteCard from "@/features/profile/components/favourite-card";
import { Gap } from "@/components/layout/gap";
import ErrorState from "@/components/state/error";

export default function ProfileFavourites() {
    const { favourites, loading, error } = useFavourites();

    return (
        <Section title="Favourites">
            <State data={favourites} loading={loading} error={error} />

            {favourites &&
                (favourites.games.length === 0 ? (
                    <ErrorState>No favourites</ErrorState>
                ) : (
                    <Column gap={Gap.Large}>
                        {favourites.games.map((game) => (
                            <FavouriteCard
                                key={game.id}
                                image={game.image}
                                title={game.title}
                                description={game.description}
                                date={new Date(
                                    game.purchasedAt,
                                ).toLocaleDateString()}
                            />
                        ))}
                    </Column>
                ))}
        </Section>
    );
}
