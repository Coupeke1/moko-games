import type {GameState} from "@/routes/game/model/game-state.ts";
import {CurrentPlayerDisplay} from "@/routes/game/components/current-player-display.tsx";
import {usePlayerProfile} from "@/routes/game/hooks/use-player-profile.ts";

interface TurnIndicatorProps {
    gameState: GameState;
}

export function TurnIndicator({gameState}: TurnIndicatorProps) {
    const {currentRole, players, aiPlayer} = gameState;
    const currentPlayer = players.find(player => player.role === currentRole);
    const { data: profile } = usePlayerProfile(currentPlayer?.id);
    const isAITurn = aiPlayer === currentRole;

    return (
        <div className="turn-indicator bg-bg-2 p-4 rounded-lg text-center flex flex-col justify-center border border-fg-2/20">
            <div className="status-message text-fg-2 text-sm mb-2">
                Current turn:
                {isAITurn && <span className="ml-2 bg-blue-500 text-white text-xs px-2 py-0.5 rounded-full">AI</span>}
            </div>
            <CurrentPlayerDisplay
                role={currentRole}
                player={currentPlayer}
                profile={profile}
            />
        </div>
    );
}