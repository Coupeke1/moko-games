import Button from "@/components/buttons/button";
import Input from "@/components/inputs/input";
import Select from "@/components/inputs/select";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import { GridSize } from "@/components/layout/grid/size";
import showToast from "@/components/toast";
import { Category } from "@/models/store/category";
import { useMutation } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useSearchParams } from "react-router";

export default function SearchBar() {
    const [params, setParams] = useSearchParams();

    const [query, setQuery] = useState("");
    const [sorting, setSorting] = useState("alphabetic");
    const [category, setCategory] = useState("all");

    useEffect(() => {
        const query: string | null = params.get("query");
        const sorting: string | null = params.get("sorting");
        const category: string | null = params.get("category");

        setQuery(query ?? "");
        setSorting(sorting ?? "alphabetic");
        setCategory(category ?? "all");
    }, [params]);

    const search = useMutation({
        mutationFn: async () => {
            const params: Record<string, string> = {};

            if (query) params.query = query;
            if (sorting) params.sorting = sorting;
            if (category) params.category = category;

            setParams(params);
        },
        onSuccess: async () => showToast("Store", "Searching..."),
        onError: (error: Error) => showToast("Store", error.message),
    });

    return (
        <Column>
            <section className={`grid sm:grid-cols-4 ${Gap.Medium}`}>
                <section className="sm:col-span-3">
                    <Input
                        placeholder="Search Store..."
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                    />
                </section>

                <section className="hidden sm:block">
                    <Button onClick={() => search.mutate()} fullWidth={true}>
                        Search
                    </Button>
                </section>
            </section>

            <Grid size={GridSize.ExtraSmall}>
                <Select
                    placeholder="Sorting"
                    options={[{ label: "Alphabetic", value: "alphabetic" }]}
                    value={sorting}
                    onChange={(e) => setSorting(e.target.value)}
                />

                <Select
                    placeholder="Category"
                    options={Object.values(Category).map((value: string) => ({
                        label: `${value.charAt(0)}${value.slice(1).toLowerCase()}`,
                        value: value.toLowerCase(),
                    }))}
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                />
            </Grid>

            <section className="sm:hidden">
                <Button onClick={() => search.mutate()} fullWidth={true}>
                    Search
                </Button>
            </section>
        </Column>
    );
}
