import {type Profile} from "../../models/profile";
import {type Player} from "../../models/player";
import {usePlayerProfile} from "../../hooks/use-player-profile";
import {PlayerRoleBadge} from "../player-role-badge";
import PlayerImage from "../player-image";

interface GameWinProps {
    myProfile: Profile;
    winningPlayer: Player
}

export function GameWin({myProfile, winningPlayer}: GameWinProps) {
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
        </div>
    )
}