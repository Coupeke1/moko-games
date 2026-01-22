import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import State from "@/components/state/state";
import SearchBar from "@/features/store/components/search-bar";
import StoreCard from "@/features/store/components/store-card";
import { useStore } from "@/features/store/hooks/use-store.ts";
import type { Entry } from "@/features/store/models/entry/entry.ts";
import { useQueryClient } from "@tanstack/react-query";

export default function StorePage() {
    const client = useQueryClient();
    const { entries, loading, error } = useStore();

    const search = (query: string, sorting: string, category: string) => {
        client.setQueryData(["store", "params"], { query, sorting, category });
        client.invalidateQueries({ queryKey: ["store"] });
    };

    return (
        <Page>
            <SearchBar
                onSearch={(query, sorting, category) =>
                    search(query, sorting, category)
                }
            />

            <State
                loading={loading}
                error={error}
                empty={entries.length === 0}
                message="No games"
            >
                <Grid>
                    {entries.map((entry: Entry) => (
                        <StoreCard key={entry.id} entry={entry} />
                    ))}
                </Grid>
            </State>
        </Page>
    );
}
