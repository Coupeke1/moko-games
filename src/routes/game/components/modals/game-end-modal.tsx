import type {GameState} from "@/routes/game/model/game-state.ts";
import type {Profile} from "@/models/profile.ts";
import {GameWin} from "@/routes/game/components/modals/game-win.tsx";
import {GameTie} from "@/routes/game/components/modals/game-tie.tsx";

interface GameEndModalProps {
    gameState: GameState;
    myProfile: Profile;
    isOpen: boolean;
}

export function GameEndModal({gameState, myProfile, isOpen}: GameEndModalProps) {
    const {winner, players} = gameState;
    const winningPlayer = players.find(player => player.id === winner);

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-bg-2 rounded-xl shadow-2xl max-w-md w-full p-6 border border-fg-2">
                {!winner ? (
                    <GameTie gameState={gameState}/>
                ) : (
                    <GameWin gameState={gameState} myProfile={myProfile} winningPlayer={winningPlayer!}/>
                )}
            </div>
        </div>
    );
}