import {PlayerRoleBadge} from "@/routes/game/components/player-role-badge.tsx";
import type {Profile} from "@/models/profile.ts";
import PlayerImage from "@/routes/game/components/player-image.tsx";

interface MyRoleDisplayProps {
    profile: Profile;
    role: string | null;
}

export function MyRoleDisplay({profile, role}: MyRoleDisplayProps) {
    if (!role) {
        return (
            <div className="bg-yellow-500 text-white px-4 py-2 rounded-lg text-sm">
                No role found for user {profile.id}
            </div>
        );
    }

    return (
        <div className="flex items-center gap-4 bg-bg-2 p-4 rounded-lg">
            <div className="flex items-center gap-3">

                <div className="flex flex-col">
                    <div className="items-center gap-2">
                        <div className="flex flex-row items-center gap-2 mb-2">
                            <PlayerImage src={profile.image} big={false} />
                            <span className="text-fg-2 text-lg">{profile.username}</span>
                        </div>
                        <span className="text-fg-2 text-sm">Your role:</span>
                        <div className="flex flex-row items-center gap-2">
                            <PlayerRoleBadge role={role}/>
                            <span className="text-fg text-xl font-bold">Player {role}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}