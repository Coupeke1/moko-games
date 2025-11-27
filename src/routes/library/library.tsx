import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import Message from "@/components/state/message";
import { useLibrary } from "@/hooks/use-library";
import type { Game } from "@/models/library/game";
import LibraryCard from "@/routes/library/components/library-card";
import SearchBar from "@/routes/library/components/search-bar";
import GameDialog from "@/routes/library/dialogs/game-dialog/game-dialog";
import { useState } from "react";

export default function LibraryPage() {
    const { games, isLoading, isError } = useLibrary();
    const [details, setDetails] = useState(false);
    const [game, setGame] = useState<Game | null>(null);

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
            {
                game && (
                    <GameDialog
                        game={game}
                        close={() => {
                            setDetails(false);
                            setGame(null);
                        }}
                        open={details}
                        onChange={setDetails}
                    />
                )
            }

            {
                games.length == 0 ? (
                    <Message>No games :(</Message>
                ) : (
                    <Grid>
                        {
                            games.map((game: Game) => (
                                <LibraryCard
                                    key={game.id}
                                    game={game}
                                    onClick={(game: Game) => {
                                        setGame(game);
                                        setDetails(true);
                                    }}
                                />
                            ))
                        }
                    </Grid>
                )
            }
        </Page>
    )
}