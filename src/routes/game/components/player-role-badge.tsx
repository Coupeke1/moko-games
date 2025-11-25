interface PlayerRoleBadgeProps {
    role: string | null;
}

export function PlayerRoleBadge({ role }: PlayerRoleBadgeProps) {
    if (!role) {
        return null;
    }

    const roleColors = {
        X: 'bg-red-500 border-red-600 text-white',
        O: 'bg-blue-500 border-blue-600 text-white'
    };

    return (
        <div className={`w-8 h-8 text-lg 
            ${roleColors[role as keyof typeof roleColors]}
            rounded-full border-2 flex items-center justify-center font-bold shadow-lg
        `}>
            {role}
        </div>
    );
}
