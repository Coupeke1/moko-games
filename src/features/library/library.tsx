import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import State from "@/components/state/state";
import LibraryCard from "@/features/library/components/library-card";
import SearchBar from "@/features/library/components/search-bar";
import { useLibrary } from "@/features/library/hooks/use-library.ts";
import type { Entry } from "@/features/library/models/entry.ts";
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

            <State
                loading={loading}
                error={error}
                empty={entries.length === 0}
                message="No games"
            >
                <Grid>
                    {entries.map((entry: Entry) => (
                        <LibraryCard key={entry.id} entry={entry} />
                    ))}
                </Grid>
            </State>
        </Page>
    );
}
