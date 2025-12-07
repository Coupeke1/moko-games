import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import Message from "@/components/state/message";
import State from "@/components/state/state";
import { useLibrary } from "@/hooks/use-library";
import type { Entry } from "@/models/library/entry";
import LibraryCard from "@/routes/library/components/library-card";
import SearchBar from "@/routes/library/components/search-bar";

export default function LibraryPage() {
    const { entries, loading, error } = useLibrary();

    return (
        <Page>
            <SearchBar />
            <State data={entries} loading={loading} error={error} />

            {entries &&
                (entries.length == 0 ? (
                    <Message>No games :(</Message>
                ) : (
                    <Grid>
                        {entries.map((entry: Entry) => (
                            <LibraryCard key={entry.id} entry={entry} />
                        ))}
                    </Grid>
                ))}
        </Page>
    );
}
