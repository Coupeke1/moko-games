interface Props {
    role: string | null;
}

export default function PlayerRoleBadge({ role }: Props) {
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
