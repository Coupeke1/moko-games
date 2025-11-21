import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import SearchBar from "@/routes/library/components/search-bar";
import LibraryCard from "@/routes/library/components/library-card";

export default function LibraryPage() {
    return (
        <Page>
            <SearchBar />

            <Grid>
                <LibraryCard
                    title="The Last Of Us"
                    image="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fs.yimg.com%2Fuu%2Fapi%2Fres%2F1.2%2Fn.oGzeisSMu4mt6vVCzHtQ--~B%2FZmk9ZmlsbDtoPTEzODk7dz0yNDcwO2FwcGlkPXl0YWNoeW9u%2Fhttps%3A%2F%2Fmedia-mbst-pub-ue1.s3.amazonaws.com%2Fcreatr-uploaded-images%2F2022-08%2Fdcb38c70-28a6-11ed-a77f-d92be29b5fc1.cf.jpg&f=1&nofb=1&ipt=dc23a485a685c490a6d45052c110eb06925c5179c4fc86fea77ee4671915f785"
                    playtime="6h 30m"
                    friendCount={3}
                />
            </Grid>
        </Page>
    )
}