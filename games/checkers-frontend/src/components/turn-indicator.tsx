import {usePlayerProfile} from "../hooks/use-player-profile";
import {type GameState} from "../models/game-state";
import {CurrentPlayerDisplay} from "./current-player-display";
import type {ReactNode} from "react";

interface TurnIndicatorProps {
    gameState: GameState;
    children: ReactNode
}

export function TurnIndicator({gameState, children}: TurnIndicatorProps) {
    const {currentRole, players, botPlayer} = gameState;
    const currentPlayer = players.find(player => player.role === currentRole);
    const {data: profile} = usePlayerProfile(currentPlayer?.id);
    const isAITurn = botPlayer === currentRole;

    return (
        <div
            className="turn-indicator bg-bg-2 p-4 rounded-lg text-center flex flex-col justify-center border border-fg-2/20">
            <div className="status-message text-fg-2 text-sm mb-2">
                Current turn:
                {isAITurn && <span className="ml-2 bg-blue-500 text-white text-xs px-2 py-0.5 rounded-full">AI</span>}
            </div>
            <CurrentPlayerDisplay
                role={currentRole}
                player={currentPlayer}
                profile={profile}
            />
            {children}
        </div>
    );
}