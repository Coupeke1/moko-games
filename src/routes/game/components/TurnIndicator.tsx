import type {GameState} from "@/routes/game/model/GameState.ts";
import {CurrentPlayerDisplay} from "@/routes/game/components/CurrentPlayerDisplay.tsx";

interface TurnIndicatorProps {
    gameState: GameState;
}

export function TurnIndicator({gameState}: TurnIndicatorProps) {
    const {currentRole, players} = gameState;
    const currentPlayer = players.find(player => player.role === currentRole);

    return (
        <div className="turn-indicator bg-bg-2 p-4 rounded text-center">
            <div className="status-message text-fg-2 text-lg mb-2">Current turn:</div>
            <CurrentPlayerDisplay
                role={currentRole}
                player={currentPlayer}
            />
        </div>
    );
}