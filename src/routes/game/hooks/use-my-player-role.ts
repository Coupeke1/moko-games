import {useGameState} from "@/routes/game/hooks/use-game-state.ts";
import {useMyProfile} from "@/routes/game/hooks/use-my-profile.ts";

export function useMyPlayerRole(gameId: string) {
    const { data: gameState } = useGameState(gameId);
    const { data: myProfile } = useMyProfile();

    if (!gameState || !myProfile) {
        return null;
    }

    const myPlayer = gameState.players.find(player => player.id === myProfile.id);
    return myPlayer?.role || null;
}