import { useEffect } from "react";
import { useNavigate, useParams } from "react-router";
import { useProfile } from "@/hooks/use-profile";
import { useLobby } from "@/hooks/use-lobby";
import { useGame } from "@/hooks/use-game";
import { isUserOwner } from "@/services/lobby-service";

export function useLobbyData() {
    const navigate = useNavigate();
    const { id } = useParams();

    useEffect(() => {
        if (!id || id.length <= 0) navigate("/library");
    }, [id, navigate]);

    const {
        profile,
        isLoading: profileLoading,
        isError: profileError,
    } = useProfile();

    const {
        lobby,
        isLoading: lobbyLoading,
        isError: lobbyError,
    } = useLobby(id, profile?.id);

    const {
        game,
        isLoading: gameLoading,
        isError: gameError,
    } = useGame(lobby?.gameId);

    const isLoading = profileLoading || lobbyLoading || gameLoading;
    const isError = profileError || lobbyError || gameError;
    const isOwner = profile && lobby ? isUserOwner(profile.id, lobby) : false;

    return { lobby, profile, game, isOwner, isLoading, isError };
}
