import Input from "@/components/inputs/input";
import Select from "@/components/inputs/select";
import Column from "@/components/layout/column";
import Page from "@/components/layout/page";

export default function StorePage() {
    return (
        <Page>
            <Column>
                <Input placeholder="Search Store..." value="" onChange={() => { }} />
                <Select value="" onChange={() => { }} options={[]} />
                <Select value="" onChange={() => { }} options={[]} />
            </Column>
        </Page>
    )
}