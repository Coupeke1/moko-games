import {PlayerRoleBadge} from "@/routes/game/components/player-role-badge.tsx";
import PlayerImage from "@/routes/game/components/player-image.tsx";
import {GameStateDisplay} from "@/routes/game/components/game-state-display.tsx";
import type {GameState} from "@/routes/game/model/game-state.ts";
import type {Profile} from "@/models/profile.ts";
import type {Player} from "@/routes/game/model/Player.ts";
import {usePlayerProfile} from "@/routes/game/hooks/use-player-profile.ts";

interface GameWinProps {
    gameState: GameState;
    myProfile: Profile;
    winningPlayer: Player
}

export function GameWin({gameState, myProfile, winningPlayer}: GameWinProps) {
    const {data: winningPlayerProfile} = usePlayerProfile(winningPlayer?.id);

    return (
        <div className="text-center">
            <div className="mb-6">
                <h2 className="text-3xl font-bold text-fg mb-2">
                    {myProfile.id === winningPlayer.id ? "You Win" : "Game Over"}
                </h2>
            </div>

            <div className="bg-bg-3 rounded-lg p-4 mb-6">
                <div className="flex flex-col items-center gap-3">
                    <div className="text-fg text-lg">Winner</div>
                    <PlayerRoleBadge role={winningPlayer?.role || null}/>
                    {winningPlayer && (
                        <div className="flex items-center gap-2 mt-2">
                            {!winningPlayerProfile ? (
                                <></>
                            ) : (
                                <div className="flex items-center gap-2">
                                    <div>
                                        <PlayerImage src={winningPlayerProfile.image} big={true}/>
                                    </div>
                                    <div className="text-fg text-lg">
                                        {winningPlayerProfile.username}
                                    </div>
                                </div>
                            )}
                        </div>
                    )}
                </div>
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