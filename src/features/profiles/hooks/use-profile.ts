import { findProfileByName } from "@/features/profiles/services/profile.ts";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useQuery } from "@tanstack/react-query";

export function useProfile(name: string) {
    const {
        isLoading: loading,
        isError: error,
        data: profile,
    } = useQuery({
        queryKey: ["profiles", name],
        queryFn: () => findProfileByName(name),
        refetchInterval: POLLING_INTERVAL,
    });

    return { loading, error, profile: profile ?? null };
}
