import { findLobby } from "@/services/lobby-service";
import { useQuery } from "@tanstack/react-query";

export function useLobby(id: string) {
    const { isLoading, isError, data } = useQuery({
        queryKey: ["lobby", id],
        queryFn: () => findLobby(id)
    });

    return { isLoading, isError, lobby: data };
}