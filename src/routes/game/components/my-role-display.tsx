import {PlayerRoleBadge} from "@/routes/game/components/player-role-badge.tsx";

interface MyRoleDisplayProps {
    userId: string;
    role: string | null;
}

export function MyRoleDisplay({userId, role}: MyRoleDisplayProps) {
    if (!role) {
        return (
            <div className="bg-yellow-500 text-white px-4 py-2 rounded-lg text-sm">
                No role found for user {userId}
            </div>
        );
    }

    return (
        <div className="flex items-center gap-4 bg-bg-2 p-4 rounded-lg border border-fg-2">
            <div className="flex items-center gap-3">
                <PlayerRoleBadge role={role}/>
                <div className="flex flex-col">
                    <span className="text-fg-2 text-sm">Your role:</span>
                    <span className="text-fg text-xl font-bold">Player {role}</span>
                    <span className="text-fg-2 text-xs mt-1">User ID: {userId}</span>
                </div>
            </div>
        </div>
    );
}