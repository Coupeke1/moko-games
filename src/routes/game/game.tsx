import { GameGrid } from "@/routes/game/components/game-grid.tsx";
import { MyRoleDisplay } from "@/routes/game/components/my-role-display.tsx";
import { TurnIndicator } from "@/routes/game/components/turn-indicator.tsx";
import { useGameState } from "@/routes/game/hooks/use-game-state.ts";
import { useMyProfile } from "@/routes/game/hooks/use-my-profile.ts";
import { useMyPlayerRole } from "@/routes/game/hooks/use-my-player-role.ts";
import { useParams } from "react-router";
import { useState } from "react";
import LoadingState from "@/components/state/loading.tsx";
import ErrorState from "@/components/state/error.tsx";
import { GameStatus } from "@/routes/game/model/game-status.ts";

export default function GamePage() {
    const { gameId } = useParams<{ gameId: string }>();
    const [selectedCell, setSelectedCell] = useState<number | null>(null);

    const { profile, isLoading: profileLoading, isError: profileError } = useMyProfile();
    const { data: gameState, isLoading: gameLoading, isError: gameError } = useGameState(gameId || "");

    const myRole = useMyPlayerRole(gameState?.players, profile?.id);

    const isAI = gameState?.aiPlayer === myRole;

    const handleCellClick = (cellIndex: number) => {
        if (selectedCell === cellIndex) {
            setSelectedCell(null);
        } else {
            setSelectedCell(cellIndex);
        }
    };

    if (profileLoading || (gameId && gameLoading)) {
        return <LoadingState />;
    }

    if (profileError || !profile) {
        return <ErrorState msg="Could not load your profile" />;
    }

    if (!gameId) {
        return (
            <div className="min-h-screen bg-bg flex flex-col items-center justify-center p-4">
                <h1 className="text-fg text-3xl font-bold mb-4">Checkers</h1>
                <p className="text-fg-2">No game ID provided. Please join a game through the lobby.</p>
            </div>
        );
    }

    if (gameError || !gameState) {
        return <ErrorState msg={`Could not load game ${gameId}`} />;
    }

    const boardSize = gameState.board.length;

    return (
        <div className="min-h-screen bg-bg flex flex-col items-center justify-center p-4 gap-6">
            <h1 className="text-fg text-3xl font-bold">Checkers</h1>

            <MyRoleDisplay
                profile={profile}
                role={myRole}
                isAI={isAI}
            />

            {gameState.status === GameStatus.RUNNING && (
                <TurnIndicator gameState={gameState} />
            )}

            <GameGrid
                board={gameState.board}
                selectedCell={selectedCell}
                validMoves={[]}
                movePath={[]}
                onCellClick={handleCellClick}
            />

            <div className="text-fg-2 text-sm">
                Board size: {boardSize}x{boardSize} | Mode: {gameState.kingMovementMode}
            </div>

            <div className="mt-2 text-fg-2 text-xs">
                {selectedCell ? `Selected cell: ${selectedCell}` : "Click a piece to select it"}
            </div>
        </div>
    );
}