import {type Profile} from "../models/profile";
import {PlayerRole} from "../models/player-role";
import PlayerImage from "./player-image";
import {PlayerRoleBadge} from "./player-role-badge";


interface MyRoleDisplayProps {
    profile: Profile;
    role: PlayerRole | null ;
    isAI?: boolean;
}

export function MyRoleDisplay({profile, role, isAI = false}: MyRoleDisplayProps) {
    if (!role) {
        return (
            <div className="bg-yellow-500/20 border border-yellow-500 text-yellow-500 px-4 py-3 rounded-lg text-sm">
                You are spectating this game
            </div>
        );
    }

    const isBlack = role === "BLACK";

    return (
        <div className="flex items-center gap-4 bg-bg-2 p-4 rounded-lg border border-fg-2/20">
            <PlayerImage src={profile.image} big={false}/>
            <div className="flex flex-col">
                <div className="flex items-center gap-2">
                    <span className="text-fg text-lg font-medium">{profile.username}</span>
                    {isAI && (
                        <span className="bg-blue-500 text-white text-xs px-2 py-0.5 rounded-full">AI</span>
                    )}
                </div>
                <div className="flex items-center gap-2 mt-1">
                    <span className="text-fg-2 text-sm">You are:</span>
                    <PlayerRoleBadge role={role}/>
                    <span className={`font-bold ${isBlack ? 'text-neutral-900' : 'text-neutral-100'}`}>
                        {role}
                    </span>
                </div>
            </div>
        </div>
    );
}