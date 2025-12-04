import { useEffect, useMemo } from "react";
import { useNavigate, useParams } from "react-router";
import { useProfile } from "@/hooks/use-profile";
import { useLobby } from "@/hooks/use-lobby";
import { useGame } from "@/hooks/use-game";
import { isUserOwner } from "@/services/lobby/lobby-service";

export function useSession() {
    const navigate = useNavigate();
    const params = useParams();

    const id = useMemo(() => params.id, [params.id]);

    useEffect(() => {
        if (!id || id.length <= 0) navigate("/library");
    }, [id, navigate]);

    const {
        profile,
        isLoading: profileLoading,
        isError: profileError,
    } = useProfile();

    const profileId = useMemo(() => profile?.id, [profile?.id]);

    const {
        lobby,
        isLoading: lobbyLoading,
        isError: lobbyError,
    } = useLobby(id, profileId);

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
