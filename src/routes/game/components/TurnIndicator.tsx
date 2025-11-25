    import type {GameState} from "@/routes/game/model/GameState.ts";

    interface TurnIndicatorProps {
        gameState: GameState;
    }

    export function TurnIndicator({gameState}: TurnIndicatorProps) {
        const {currentRole, players} = gameState;

        const currentPlayer = players.find(player => player.role === currentRole);

        if (!currentPlayer) {
            return (
                <div className="turn-indicator bg-bg-2 p-4 rounded text-center">
                    <div className="status-message text-fg-2 text-lg mb-2">Current turn:</div>
                    <div className="current-player flex flex-col items-center gap-2">
                        <div className={`player-role text-4xl font-bold ${
                            currentRole === 'X' ? 'text-red-500' : 'text-blue-500'
                        }`}>
                            {String(currentRole)}
                        </div>
                        <div className="player-info text-fg-2">
                            <div className="text-yellow-500 text-sm">Player information unavailable</div>
                            <div className="text-xs">Role: {currentRole}</div>
                        </div>
                    </div>
                </div>
            );
        }

        const playerRole = currentPlayer?.role || currentRole;

        return (
            <div className="turn-indicator bg-bg-2 p-4 rounded text-center">
                <div className="status-message text-fg-2 text-lg mb-2">Current turn:</div>
                <div className="current-player flex flex-col items-center gap-2">
                    <div className={`player-role text-4xl font-bold ${
                        playerRole === 'X' ? 'text-red-500' : 'text-blue-500'
                    }`}>
                        {String(playerRole)}
                    </div>
                    {currentPlayer && (
                        <div className="player-id text-fg-2">
                            Player ID: {currentPlayer.id.toString() || 'Unknown'}
                        </div>
                    )}
                </div>
            </div>
        )
    }