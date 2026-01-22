import {type GameState} from "@/models/game-state.ts";
import {type Profile} from "@/models/profile.ts";
import {GameStatus} from "@/models/game-status.ts";
import {PlayerRole} from "@/models/player-role.ts";
import {GameTie} from "./game-tie";
import {GameStateDisplay} from "../game-state-display";
import {GameWin} from "./game-win";


interface GameEndModalProps {
    gameState: GameState;
    myProfile: Profile;
    isOpen: boolean;
}

export function GameEndModal({gameState, myProfile, isOpen}: GameEndModalProps) {
    const {status, players} = gameState;

    const winnerRole = status === GameStatus.WHITE_WIN
        ? PlayerRole.WHITE
        : status === GameStatus.BLACK_WIN
            ? PlayerRole.BLACK
            : null;

    const winningPlayer = winnerRole
        ? players.find(player => player.role === winnerRole)
        : undefined;

    if (!isOpen) return null;

    return (
        <>
            <div className="fixed inset-0 bg-black/60 z-40"/>

            <div className="fixed inset-0 flex items-center justify-center z-50 p-4 pointer-events-none">
                <div
                    className="pointer-events-auto bg-bg-2/95 backdrop-blur-sm rounded-xl shadow-2xl max-w-md w-full p-6 border border-fg-2/30">
                    {status === GameStatus.DRAW ? (
                        <GameTie/>
                    ) : (
                        <GameWin myProfile={myProfile} winningPlayer={winningPlayer!}/>
                    )}
                    <GameStateDisplay gameState={gameState}/>
                </div>
            </div>
        </>
    );
}