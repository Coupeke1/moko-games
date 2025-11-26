import Button from "@/components/buttons/button";
import Input from "@/components/inputs/input";
import { Gap } from "@/components/layout/gap";
import { useState } from "react";

export default function SearchBar() {
    const [query, setQuery] = useState("");

    return (
        <section className={`grid sm:grid-cols-4 ${Gap.Medium}`}>
            <section className="sm:col-span-3">
                <Input placeholder="Search Library..." value={query} onChange={(e) => setQuery(e.target.value)} />
            </section>

            <Button>Search</Button>
        </section>
    )
}