import type {GameState} from "@/routes/game/model/game-state.ts";
import {CurrentPlayerDisplay} from "@/routes/game/components/current-player-display.tsx";

interface TurnIndicatorProps {
    gameState: GameState;
}

export function TurnIndicator({gameState}: TurnIndicatorProps) {
    const {currentRole, players} = gameState;
    const currentPlayer = players.find(player => player.role === currentRole);

    return (
        <div className="turn-indicator bg-bg-2 p-4 rounded-lg text-center flex flex-col justify-center">
            <div className="status-message text-fg-2 text-lg">Current turn:</div>
            <CurrentPlayerDisplay
                role={currentRole}
                player={currentPlayer}
            />
        </div>
    );
}