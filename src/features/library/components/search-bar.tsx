import Button from "@/components/buttons/button";
import Input from "@/components/inputs/input";
import { Gap } from "@/components/layout/gap";
import showToast from "@/components/toast";
import { useMutation } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useSearchParams } from "react-router";

interface Props {
    onSearch: (query: string) => void;
}

export default function SearchBar({ onSearch }: Props) {
    const [params, setParams] = useSearchParams();
    const [query, setQuery] = useState("");

    useEffect(() => {
        const query: string | null = params.get("query");
        setQuery(query ?? "");
        onSearch(query ?? "");
    }, [params, onSearch]);

    const search = useMutation({
        mutationFn: async () => {
            const params: Record<string, string> = {};
            if (query) params.query = query;

            setParams(params);
            onSearch(query);
        },
        onSuccess: async () => showToast("Library", "Searching..."),
        onError: (error: Error) => showToast("Library", error.message),
    });

    return (
        <section className={`grid sm:grid-cols-4 ${Gap.Medium}`}>
            <section className="sm:col-span-3">
                <Input
                    placeholder="Search Library..."
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                />
            </section>

            <Button onClick={() => search.mutate()} fullWidth={true}>
                Search
            </Button>
        </section>
    );
}
