import Button from "@/components/buttons/button";
import Input from "@/components/inputs/input";
import { Gap } from "@/components/layout/gap";
import { useState } from "react";

export default function SearchBar() {
    const [query, setQuery] = useState("");

    return (
        <section className={`grid sm:grid-cols-8 ${Gap.Medium}`}>
            <section className="sm:col-span-5">
                <Input
                    placeholder="Search Store..."
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                />
            </section>

            <section
                className={`grid sm:col-span-3 xss:grid-cols-2 sm:grid-cols-3 ${Gap.Medium}`}
            >
                <Button>Filters</Button>
                <section className="sm:col-span-2">
                    <Button fullWidth={true}>Search</Button>
                </section>
            </section>
        </section>
    );
}
