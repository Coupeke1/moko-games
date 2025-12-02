import type {Profile} from "@/models/profile.ts";
import type {Player} from "@/models/player";
import PlayerCard from "../player-card";

interface GameWinProps {
    myProfile: Profile;
    winningPlayer: Player
}

export default function GameWin({myProfile, winningPlayer}: GameWinProps) {
    return (
        <div className="text-center">
            <div className="mb-6">
                <h2 className="text-3xl font-bold text-fg mb-2">
                    {myProfile.id === winningPlayer.id ? "You Win" : "Game Over"}
                </h2>
            </div>

            <div className="bg-bg-2 rounded-lg p-4 mb-6">
                <div className="grid grid-cols-1 items-center gap-3">
                    <div className="text-fg text-lg">Winner</div>
                    {winningPlayer && (
                        <PlayerCard player={winningPlayer} />
                    )}
                </div>
            </div>
        </div>
    )
}