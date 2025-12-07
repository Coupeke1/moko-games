import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import Message from "@/components/state/message";
import State from "@/components/state/state";
import { useStore } from "@/hooks/use-store";
import type { Entry } from "@/models/store/entry";
import SearchBar from "@/routes/store/components/search-bar";
import StoreCard from "@/routes/store/components/store-card";

export default function StorePage() {
    const { entries, loading, error } = useStore();

    return (
        <Page>
            <SearchBar />
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
