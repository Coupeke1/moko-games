interface PlayerDisplayProps {
    role: string;
    player?: {
        id: string;
        role: string;
        name?: string;
    };
}

export function CurrentPlayerDisplay({role, player}: PlayerDisplayProps) {
    const displayRole = player?.role || role;
    const playerId = player?.id?.toString() || 'Unknown';

    return (
        <div className="current-player flex flex-col items-center gap-2">
            <div className={`player-role text-4xl font-bold
                ${displayRole === 'X' ? 'text-x' : 'text-o'}`}>
                {String(displayRole)}
            </div>
            <div className="player-info text-fg-2">
                {!player ? (
                    <>
                        <div className="text-yellow-500 text-sm mb-1">Player information unavailable</div>
                        <div className="text-xs">Role: {role}</div>
                    </>
                ) : (
                    <div className="player-id">
                        Player ID: {playerId}
                    </div>
                )}
            </div>
        </div>
    );
}