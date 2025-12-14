import Button from "@/components/buttons/button";
import Form from "@/components/inputs/form";
import Input from "@/components/inputs/input";
import Select from "@/components/inputs/select";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import { GridSize } from "@/components/layout/grid/size";
import showToast from "@/components/toast";
import { Category, format } from "@/features/store/models/entry/category.ts";
import { useMutation } from "@tanstack/react-query";
import { useEffect, useState, type FormEvent } from "react";
import { useSearchParams } from "react-router";

interface Props {
    onSearch: (query: string, sorting: string, category: string) => void;
}

export default function SearchBar({ onSearch }: Props) {
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
            onSearch(query, sorting, category);
        },
        onError: (error: Error) => showToast("Store", error.message),
    });

    const handleSearch = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        search.mutate();
    };

    return (
        <Form submit={handleSearch}>
            <section className={`grid sm:grid-cols-4 ${Gap.Medium}`}>
                <section className="sm:col-span-3">
                    <Input
                        placeholder="Search Store..."
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                    />
                </section>

                <section className="hidden sm:block">
                    <Button fullWidth={true}>Search</Button>
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
                    options={[
                        { label: "All Categories", value: "all" },
                        ...Object.values(Category).map((value: string) => ({
                            label: format(value),
                            value: value.toLowerCase(),
                        })),
                    ]}
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                />
            </Grid>

            <section className="sm:hidden">
                <Button fullWidth={true}>Search</Button>
            </section>
        </Form>
    );
}
