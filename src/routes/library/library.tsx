import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import SearchBar from "@/routes/library/components/search-bar";
import LibraryCard from "@/routes/library/components/card";
import { useLibrary } from "@/hooks/use-library";
import LoadingState from "@/components/state/loading";
import ErrorState from "@/components/state/error";
import Message from "@/components/state/message";
import type { Game } from "@/models/library/game";

export default function LibraryPage() {
    const { games, isLoading, isError } = useLibrary();

    if (isLoading || games === undefined) return (
        <Page>
            <SearchBar />
            <LoadingState />
        </Page>
    );

    if (isError) return (
        <Page>
            <ErrorState />
        </Page>
    );

    return (
        <Page>
            <SearchBar />

            <Grid>
                {
                    games.length == 0 ? (
                        <Message>No games :(</Message>
                    ) : (
                        <Grid>
                            {
                                games.map((game: Game) => (
                                    <LibraryCard
                                        title={game.title}
                                        image={game.image}
                                        playtime="6h 30m"
                                        friendCount={3}
                                    />
                                ))
                            }
                        </Grid>
                    )
                }
            </Grid>
        </Page>
    )
}