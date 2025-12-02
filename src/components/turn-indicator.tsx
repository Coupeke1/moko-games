import type {GameState} from "@/models/game-state";
import {usePlayerProfile} from "@/hooks/use-player-profile";
import { useMyProfile } from "@/hooks/use-my-profile";

interface Props {
    gameState: GameState;
}

export default function TurnIndicator({gameState}: Props) {
    const { profile: myProfile} = useMyProfile();
    const {currentRole, players} = gameState;
    const currentPlayer = players.find(player => player.role === currentRole);
    const { data: otherProfile } = usePlayerProfile(currentPlayer?.id);

    return (
        <p className="text-fg-2 text-lg">It's <span className="text-fg">{myProfile?.id === otherProfile?.id ? 'your' : `${otherProfile?.username}'s`}</span> turn!</p>
    );
}