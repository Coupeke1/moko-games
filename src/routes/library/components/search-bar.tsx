import Input from "@/components/inputs/input";
import Select from "@/components/inputs/select";
import { Gap } from "@/components/layout/gap";

export default function SearchBar() {
    return (
        <section className={`flex flex-col sm:flex-row justify-between ${Gap.Medium}`}>
            <Input label="Search Library..." value="" onChange={() => { }} />
            <section className={`flex flex-col xss:flex-row ${Gap.Medium}`}>
                <Select label="Sorting" value="" onChange={() => { }} options={[]} />
                <Select label="Category" value="" onChange={() => { }} options={[]} />
            </section>
        </section>
    )
}