import type {GameState} from "@/models/game-state";
import {usePlayerProfile} from "@/hooks/use-player-profile";
import CurrentPlayerDisplay from "@/components/current-player-display.tsx";

interface Props {
    gameState: GameState;
}

export default function TurnIndicator({gameState}: Props) {
    const {currentRole, players} = gameState;
    const currentPlayer = players.find(player => player.role === currentRole);
    const { data: profile } = usePlayerProfile(currentPlayer?.id);

    return (
        <div className="turn-indicator bg-bg-2 p-4 rounded-lg text-center flex flex-col justify-center">
            <div className="status-message text-fg-2 text-lg">Current turn:</div>
            <CurrentPlayerDisplay
                role={currentRole}
                player={currentPlayer}
                profile={profile}
            />
        </div>
    );
}