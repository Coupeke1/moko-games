import { useEffect, useMemo } from "react";
import { useNavigate, useParams } from "react-router";
import { useProfile } from "@/features/profile/hooks/use-profile.ts";
import { useLobby } from "@/features/lobby/hooks/use-lobby.ts";
import { useGame } from "@/features/games/hooks/use-game";
import { isUserOwner } from "@/features/lobby/services/lobby.ts";

export function useSession() {
    const navigate = useNavigate();
    const params = useParams();

    const id = useMemo(() => params.id, [params.id]);

    useEffect(() => {
        if (!id || id.length <= 0) navigate("/library");
    }, [id, navigate]);

    const {
        profile,
        loading: profileLoading,
        error: profileError,
    } = useProfile();

    const profileId = useMemo(() => profile?.id, [profile?.id]);

    const {
        lobby,
        loading: lobbyLoading,
        error: lobbyError,
    } = useLobby(id, profileId);

    const {
        game,
        loading: gameLoading,
        error: gameError,
    } = useGame(lobby?.gameId);

    const loading = profileLoading || lobbyLoading || gameLoading;
    const error = profileError || lobbyError || gameError;
    const isOwner = profile && lobby ? isUserOwner(profile.id, lobby) : false;

    return { lobby, profile, game, isOwner, loading, error };
}
