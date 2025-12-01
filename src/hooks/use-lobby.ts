import { findLobby, isPlayerInLobby } from "@/services/lobby-service";
import { useQuery } from "@tanstack/react-query";
import { useEffect } from "react";
import { useNavigate } from "react-router";

export function useLobby(
    lobbyId: string | undefined,
    userId: string | undefined,
) {
    const navigate = useNavigate();

    const { isLoading, isError, data } = useQuery({
        queryKey: ["lobby", lobbyId],
        queryFn: () => findLobby(lobbyId!),
        enabled: !!lobbyId && !!userId,
    });

    useEffect(() => {
        if (data && !isPlayerInLobby(userId!, data)) navigate("/library");
    }, [data, navigate, userId]);

    return { isLoading, isError, lobby: data };
}
