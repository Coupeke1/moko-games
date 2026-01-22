import { useQuery } from "@tanstack/react-query";
import { findSchema } from "../services/settings";

export function useSchema(id: string) {
    const query = useQuery({
        queryKey: ["game", "schema", id],
        enabled: !!id,
        queryFn: () => findSchema(id),
    });

    return {
        schema: query.data ?? null,
        loading: query.isLoading && Boolean(id),
        error: query.isError,
    };
}
