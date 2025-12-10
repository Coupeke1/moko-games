import {PlayerRoleBadge} from "@/routes/game/components/player-role-badge.tsx";
import type {Profile} from "@/models/profile.ts";
import PlayerImage from "@/routes/game/components/player-image.tsx";
import type {PlayerRole} from "@/routes/game/model/player-role.ts";

interface MyRoleDisplayProps {
    profile: Profile;
    role: PlayerRole | null;
    isAI?: boolean;
}

export function MyRoleDisplay({profile, role, isAI = false}: MyRoleDisplayProps) {
    if (!role) {
        return (
            <div className="bg-yellow-500 text-white px-4 py-2 rounded-lg text-sm">
                No role found for user {profile.id}
            </div>
        );
    }

    return (
        <div className="flex items-center gap-4 bg-bg-2 p-4 rounded-lg">
            <div className="flex flex-col items-center">
                <div className="flex flex-row items-center gap-2 mb-2">
                    <PlayerImage src={profile.image} big={false}/>
                    <span className="text-fg-2 text-lg">{profile.username}</span>
                    {isAI && (
                        <span className="bg-blue-500 text-white text-xs px-2 py-1 rounded-full">AI</span>
                    )}
                </div>
                <div className="items-center">
                    <span className="text-fg-2 text-sm">Your role:</span>
                    <div className="flex flex-row items-center gap-2">
                        <PlayerRoleBadge role={role}/>
                        <span className="text-fg text-xl font-bold">Player {role}</span>
                    </div>
                </div>
            </div>
        </div>
    );
}