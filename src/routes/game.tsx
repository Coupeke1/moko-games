import {useParams} from "react-router";
import {useState} from "react";
import {useMyProfile} from "../hooks/use-my-profile";
import {useGameState} from "../hooks/use-game-state";
import {useMyPlayerRole} from "../hooks/use-my-player-role";
import LoadingState from "../components/state/loading";
import ErrorState from "../components/state/error";
import {MyRoleDisplay} from "../components/my-role-display";
import {GameStatus} from "../models/game-status";
import {TurnIndicator} from "../components/turn-indicator";
import {GameGrid} from "../components/game-grid";


export default function GamePage() {
    const {id} = useParams<{ id: string }>();
    const [selectedCell, setSelectedCell] = useState<number | null>(null);

    const {profile, isLoading: profileLoading, isError: profileError} = useMyProfile();
    const {data: gameState, isLoading: gameLoading, isError: gameError} = useGameState(id || "");

    const myRole = useMyPlayerRole(gameState?.players, profile?.id);

    const isAI = gameState?.aiPlayer === myRole;

    const handleCellClick = (cellIndex: number) => {
        if (selectedCell === cellIndex) {
            setSelectedCell(null);
        } else {
            setSelectedCell(cellIndex);
        }
    };

    if (profileLoading || (id && gameLoading)) {
        return <LoadingState/>;
    }

    if (profileError || !profile) {
        return <ErrorState msg="Could not load your profile"/>;
    }

    if (!id) {
        return (
            <div className="min-h-screen bg-bg flex flex-col items-center justify-center p-4">
                <h1 className="text-fg text-3xl font-bold mb-4">Checkers</h1>
                <p className="text-fg-2">No game ID provided. Please join a game through the lobby.</p>
            </div>
        );
    }

    if (gameError || !gameState) {
        return <ErrorState msg={`Could not load game ${id}`}/>;
    }

    const boardSize = 8

    return (
        <div className="min-h-screen bg-bg flex flex-col items-center justify-center p-4 gap-6">
            <h1 className="text-fg text-3xl font-bold">Checkers</h1>

            <MyRoleDisplay
                profile={profile}
                role={myRole}
                isAI={isAI}
            />

            {gameState.status === GameStatus.RUNNING && (
                <TurnIndicator gameState={gameState}/>
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