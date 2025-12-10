import {PlayerRole} from "../models/player-role";

interface PlayerRoleBadgeProps {
    role: PlayerRole | null;
}

export function PlayerRoleBadge({role}: PlayerRoleBadgeProps) {
    if (!role) {
        return null;
    }

    const isBlack = role === 'BLACK';

    return (
        <div className="flex items-center gap-2">
            <div
                className={`w-10 h-10 rounded-full ${isBlack ? 'bg-piece-black' : 'bg-piece-white'} border-2 ${isBlack ? 'border-piece-black-highlight' : 'border-piece-white-highlight'} shadow-md`}
            />
        </div>
    );
}