import {useParams} from "react-router";
import {useMyProfile} from "../hooks/use-my-profile";
import {useGameState} from "../hooks/use-game-state";
import {useMyPlayerRole} from "../hooks/use-my-player-role";
import LoadingState from "../components/state/loading";
import ErrorState from "../components/state/error";
import {MyRoleDisplay} from "../components/my-role-display";
import {GameStatus} from "../models/game-status";
import {TurnIndicator} from "../components/turn-indicator";
import {GameGrid} from "../components/game-grid";
import {requestMove} from "../services/game-service";
import {toast} from "sonner";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {ConfirmTurnButton} from "@/components/confirm-turn-button.tsx";
import {useMakeMoveSelection} from "@/hooks/use-make-move-selection.ts";

export default function GamePage() {
    const {id} = useParams<{ id: string }>();

    const {profile, isLoading: profileLoading, isError: profileError} = useMyProfile();
    const {data: gameState, isLoading: gameLoading, isError: gameError} = useGameState(id || "");
    const queryClient = useQueryClient();

    const myRole = useMyPlayerRole(gameState?.players, profile?.id);
    const isAI = gameState?.aiPlayer === myRole;
    const isMyTurn = gameState?.currentRole === myRole;

    const {
        selectedCell,
        movePath,
        handleCellClick,
        canConfirmMove,
        setSelectedCell,
        setMovePath
    } = useMakeMoveSelection(
        isMyTurn,
        gameState?.status,
        myRole,
        gameState?.board
    );

    const moveMutation = useMutation({
        mutationFn: (cells: number[]) => requestMove(id!, profile!.id, cells),
        onSuccess: () => {
            setSelectedCell(null);
            setMovePath([]);
            queryClient.invalidateQueries({queryKey: ['gameState', id]});
            toast.success("Move successful!");
        },
        onError: (error: Error) => {
            toast.error(`Move failed: ${error.message}`);
        }
    });

    const handleConfirmMove = () => {
        if (movePath.length < 2) {
            toast.error("Select at least 2 cells to make a move");
            return;
        }

        if (!selectedCell || !profile?.id || !id) {
            toast.error("Cannot make move. Please try again.");
            return;
        }

        moveMutation.mutate(movePath);
    }

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

    const boardSize = gameState.board.length;

    return (
        <div className="min-h-screen bg-bg flex flex-col md:flex-row p-10 gap-12">

            {/* Sidebar */}
            <aside className="md:w-[28rem] w-full flex flex-col gap-8">

                <h1 className="text-fg text-5xl font-extrabold tracking-tight drop-shadow text-center md:text-left">
                    Checkers
                </h1>

                <div className="flex flex-col gap-4">
                    <div
                        className="bg-bg-1/70 p-6 rounded-2xl border border-fg-2/10 backdrop-blur-sm text-sm text-fg-2">
                        <h2 className="text-fg font-semibold text-lg mb-2">Game Details</h2>
                        <div className="space-y-1">
                            <p>Board size: {boardSize}x{boardSize}</p>
                            <p>Mode: {gameState.kingMovementMode}</p>
                        </div>
                    </div>

                    <div
                        className="bg-bg-1/70 flex flex-col p-6 rounded-2xl border border-fg-2/10 backdrop-blur-sm gap-4">
                        <MyRoleDisplay profile={profile} role={myRole} isAI={isAI}/>

                        {gameState.status === GameStatus.RUNNING && (
                            <TurnIndicator gameState={gameState}>
                                <ConfirmTurnButton onClick={handleConfirmMove} disabled={!canConfirmMove}>
                                    Confirm Move
                                </ConfirmTurnButton>
                            </TurnIndicator>
                        )}
                    </div>
                </div>
            </aside>

            {/* Game Board */}
            <main className="flex-1 flex flex-col items-center justify-center">
                <div className="w-full max-w-[min(90vw,52rem)] mx-auto">
                    <GameGrid
                        board={gameState.board}
                        selectedCell={selectedCell}
                        validMoves={[]}
                        movePath={movePath}
                        onCellClick={handleCellClick}
                    />
                </div>

                <div className="mt-6 text-fg-2 text-sm text-center">
                    {!isMyTurn
                        ? "Waiting for opponent..."
                        : selectedCell
                            ? `Selected cell: ${selectedCell} - Click any cell to move`
                            : "Click a piece to select it"}
                </div>
            </main>

        </div>
    );
}