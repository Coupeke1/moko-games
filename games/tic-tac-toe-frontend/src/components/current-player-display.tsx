import type {Profile} from "@/models/profile.ts";
import type {Player} from "@/models/player";
import PlayerImage from "@/components/player-image.tsx";

interface Props {
    role: string;
    player?: Player;
    profile?: Profile;
}

export default function CurrentPlayerDisplay({role, player, profile}: Props) {
    return (
        <div className="items-center">
            <div className={`player-role text-5xl font-bold
                ${player?.role === 'X' ? 'text-x' : 'text-o'}`}>
                {String(player?.role)}
            </div>
            <div className="player-info text-fg-2 mt-4">
                {!player ? (
                    <>
                        <div className="text-yellow-500 text-sm mb-1">Player information unavailable</div>
                        <div className="text-xs">Role: {role}</div>
                    </>
                ) : (
                    <div className="flex flex-col items-center">
                        <div className="flex flex-row items-center gap-2 mb-2">
                            {!profile ? (
                                <></>
                            ) : (
                                <>
                                    <PlayerImage src={profile.image} big={false}/>
                                    <span className="text-fg-2 text-lg">{profile.username}</span>
                                </>
                            )}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}