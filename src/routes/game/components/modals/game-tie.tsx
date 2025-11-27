import {GameStateDisplay} from "@/routes/game/components/game-state-display.tsx";
import type {GameState} from "@/routes/game/model/game-state.ts";

interface GameTieProps {
    gameState: GameState;
}

export function GameTie({gameState}: GameTieProps) {
    return (
        <div className="text-center">
            <div className="mb-6">
                <h2 className="text-3xl font-bold text-fg mb-2">
                   Tie
                </h2>
            </div>

            <GameStateDisplay gameState={gameState}/>

            <div className="flex gap-3">
                <button
                    //todo: onClick={() => }
                    className="flex-1 bg-x text-white py-3 px-4 rounded-lg font-medium hover:bg-opacity-90 transition-colors"
                >
                    New game
                </button>
            </div>
        </div>
    )
}