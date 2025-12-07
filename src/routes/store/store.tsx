import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import Message from "@/components/state/message";
import State from "@/components/state/state";
import { useStore } from "@/hooks/use-store";
import type { Entry } from "@/models/store/entry";
import SearchBar from "@/routes/store/components/search-bar";
import StoreCard from "@/routes/store/components/store-card";
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

            <State data={entries} loading={loading} error={error} />

            {entries &&
                (entries.items.length == 0 ? (
                    <Message>No games :(</Message>
                ) : (
                    <Grid>
                        {entries.items.map((entry: Entry) => (
                            <StoreCard key={entry.id} entry={entry} />
                        ))}
                    </Grid>
                ))}
        </Page>
    );
}
