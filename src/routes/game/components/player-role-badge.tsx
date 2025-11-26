interface PlayerRoleBadgeProps {
    role: string | null;
}

export function PlayerRoleBadge({ role }: PlayerRoleBadgeProps) {
    if (!role) {
        return null;
    }

    return (
        <div className={`player-role text-5xl font-bold
                ${role === 'X' ? 'text-x' : 'text-o'}`}>
            {role}
        </div>
    );
}
