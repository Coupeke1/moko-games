import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Section from "@/components/section";
import State from "@/components/state/state";
import type { Entry } from "@/features/library/models/entry";
import FavouriteCard from "@/features/profiles/components/favourite-card";
import type { Favourite } from "@/features/profiles/models/favourite";

interface Props {
    favourites: Favourite[];
    loading?: boolean;
    error?: boolean;
}

export default function ProfileFavourites({
    favourites,
    loading,
    error,
}: Props) {
    return (
        <Section title="Favourites">
            <State
                loading={loading ?? false}
                error={error ?? false}
                empty={favourites.length === 0}
                message="No favourites"
            >
                {favourites && (
                    <Grid>
                        {favourites.map((favourite: Favourite) => (
                            <FavouriteCard
                                key={favourite.id}
                                favourite={favourite}
                            />
                        ))}
                    </Grid>
                )}
            </State>
        </Section>
    );
}
