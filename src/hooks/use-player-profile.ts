import type {Profile} from "@/models/profile.ts";
import {findProfile} from "@/services/profile-service.ts";
import {useQuery} from "@tanstack/react-query";

export function usePlayerProfile(playerId: string | undefined) {
    return useQuery<Profile, Error>({
        queryKey: ['playerProfile', playerId],
        queryFn:  () => findProfile(playerId!),
        enabled:  !!playerId,
        staleTime: 30_000,
    });
}