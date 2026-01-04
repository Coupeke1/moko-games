import { findMyAchievements } from "@/features/profiles/services/achievement";
import { POLLING_INTERVAL } from "@/lib/polling";
import { useQuery } from "@tanstack/react-query";

export function useAchievements(id: string) {
    const {
        data,
        isLoading: loading,
        isError: error,
    } = useQuery({
        queryKey: ["achievements", id],
        queryFn: async () => await findMyAchievements(),
        enabled: !!id,
        refetchInterval: POLLING_INTERVAL,
    });

    return { achievements: data ?? [], loading, error };
}
