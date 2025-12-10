import {PlayerRole} from "../models/player-role";

interface PlayerRoleBadgeProps {
    role: PlayerRole | null;
    size?: "sm" | "md";
}

export function PlayerRoleBadge({role, size = "md"}: PlayerRoleBadgeProps) {
    if (!role) return null;

    const isBlack = role === "BLACK";
    const dim = size === "sm" ? "w-5 h-5" : "w-8 h-8";

    return (
        <div
            className={`
                ${dim} rounded-full 
                ${isBlack ? "bg-piece-black" : "bg-piece-white"} 
                border 
                ${isBlack ? "border-piece-black-highlight" : "border-piece-white-highlight"}
                shadow-sm
            `}
        />
    );
}