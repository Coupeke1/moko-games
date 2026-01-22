import type {Profile} from "@/models/profile.ts";
import {PlayerRole} from "../models/player-role";
import {type Player} from "../models/player";
import PlayerImage from "./player-image";

interface PlayerDisplayProps {
    role: PlayerRole;
    player?: Player;
    profile?: Profile;
}

export function CurrentPlayerDisplay({role, player, profile}: PlayerDisplayProps) {
    const isBlack = player?.role === 'BLACK';

    return (
        <div className="items-center">
            <div className="flex items-center justify-center gap-2">
                <div
                    className={`w-8 h-8 rounded-full ${isBlack ? 'bg-piece-black' : 'bg-piece-white'} border-2 ${isBlack ? 'border-piece-black-highlight' : 'border-piece-white-highlight'}`}
                />
                <span className="text-fg text-xl font-bold">{player?.role}</span>
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