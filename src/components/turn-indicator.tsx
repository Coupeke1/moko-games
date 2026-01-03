import type {GameState} from "@/models/game-state";
import {usePlayerProfile} from "@/hooks/use-player-profile";
import { useMyProfile } from "@/hooks/use-my-profile";
import type {ReactNode} from "react";

interface Props {
    gameState: GameState;
    children: ReactNode
}

export default function TurnIndicator({gameState, children}: Props) {
    const { profile: myProfile} = useMyProfile();
    const {currentRole, players} = gameState;
    const currentPlayer = players.find(player => player.role === currentRole);
    const { data: otherProfile } = usePlayerProfile(currentPlayer?.id);

    return (
        <div
        className="turn-indicator bg-bg-2 p-4 rounded-lg text-center flex flex-col justify-center border border-fg-2/20">
            <p className="text-fg-2 text-lg">It's <span className="text-fg">{myProfile?.id === otherProfile?.id ? 'your' : `${otherProfile?.username}'s`}</span> turn!</p>
            {children}
        </div>
    );
}