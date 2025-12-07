import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import Message from "@/components/state/message";
import State from "@/components/state/state";
import { useLibrary } from "@/hooks/use-library";
import type { Entry } from "@/models/library/entry";
import LibraryCard from "@/routes/library/components/library-card";
import SearchBar from "@/routes/library/components/search-bar";
import { useQueryClient } from "@tanstack/react-query";

export default function LibraryPage() {
    const client = useQueryClient();
    const { entries, loading, error } = useLibrary();

    const search = (query: string) => {
        client.setQueryData(["library", "params"], { query });
        client.invalidateQueries({ queryKey: ["library"] });
    };

    return (
        <Page>
            <SearchBar onSearch={(query) => search(query)} />

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
