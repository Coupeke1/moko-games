import Button from "@/components/buttons/button";
import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import Message from "@/components/state/message";
import State from "@/components/state/state";
import { useLibrary } from "@/hooks/use-library";
import type { Game } from "@/models/library/game";
import LibraryCard from "@/routes/library/components/library-card";
import SearchBar from "@/routes/library/components/search-bar";

export default function LibraryPage() {
    const { games, loading, error } = useLibrary();

    return (
        <Page>
            <SearchBar />
            <State data={games} loading={loading} error={error} />

            {games &&
                (games.length == 0 ? (
                    <Message>No games :(</Message>
                ) : (
                    <Grid>
                        {games.map((game: Game) => (
                            <LibraryCard key={game.id} game={game} />
                        ))}
                    </Grid>
                ))}
        </Page>
    );
}
