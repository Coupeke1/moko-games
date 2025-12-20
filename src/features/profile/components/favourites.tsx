import Section from "@/components/section";
import State from "@/components/state/state";
import Column from "@/components/layout/column";
import { useFavourites } from "@/features/profile/hooks/use-favourites";
import FavouriteCard from "@/features/profile/components/favourite-card";
import { Gap } from "@/components/layout/gap";
import type { Entry } from "@/features/library/models/entry";

export default function ProfileFavourites() {
    const { favourites, loading, error } = useFavourites();

    return (
        <Section title="Favourites">
            <State
                loading={loading}
                error={error}
                empty={favourites.length === 0}
                message="No favourites"
            >
                {favourites && (
                    <Column gap={Gap.Large}>
                        {favourites.map((entry: Entry) => (
                            <FavouriteCard
                                key={entry.id}
                                image={entry.image}
                                title={entry.title}
                                description={entry.description}
                            />
                        ))}
                    </Column>
                )}
            </State>
        </Section>
    );
}
