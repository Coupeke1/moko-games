import type {GameState} from "@/routes/game/model/game-state.ts";
import type {Profile} from "@/models/profile.ts";
import {GameWin} from "@/routes/game/components/modals/game-win.tsx";
import {GameTie} from "@/routes/game/components/modals/game-tie.tsx";
import {GameStateDisplay} from "@/routes/game/components/game-state-display.tsx";

interface GameEndModalProps {
    gameState: GameState;
    myProfile: Profile;
    isOpen: boolean;
    onReset: () => void;
}

export function GameEndModal({gameState, myProfile, isOpen, onReset}: GameEndModalProps) {
    const {winner, players} = gameState;
    const winningPlayer = players.find(player => player.id === winner);

    if (!isOpen) return null;

    return (
        <>
            <div className="fixed inset-0 bg-black/60 z-40"/>

            <div className="fixed inset-0 flex items-center justify-center z-50 p-4 pointer-events-none">
                <div
                    className="pointer-events-auto bg-bg-2/95 backdrop-blur-sm rounded-xl shadow-2xl max-w-md w-full p-6 border border-fg-2/30">
                    {!winner ? (
                        <GameTie/>
                    ) : (
                        <GameWin myProfile={myProfile} winningPlayer={winningPlayer!}/>
                    )}
                    <GameStateDisplay gameState={gameState}/>

                    <div className="flex gap-3">
                        <button
                            onClick={onReset}
                            className="flex-1 bg-x text-white py-3 px-4 rounded-lg font-medium hover:bg-opacity-90 transition-colors"
                        >
                            New game
                        </button>
                    </div>
                </div>
            </div>
        </>
    );
}