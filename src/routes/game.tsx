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
import {ConfirmTurnButton} from "@/components/confirm-turn-button.tsx";
import {useMakeMoveSelection} from "@/hooks/use-make-move-selection.ts";
import {useMakeMove} from "@/hooks/use-make-move.ts";
import Page from "@/components/layout/page.tsx";
import {GameEndModal} from "@/components/dialogs/game-end-modal.tsx";

export default function GamePage() {
    const {id} = useParams<{ id: string }>();

    const {profile, isLoading: profileLoading, isError: profileError} = useMyProfile();
    const {data: gameState, isLoading: gameLoading, isError: gameError} = useGameState(id || "");

    const myRole = useMyPlayerRole(gameState?.players, profile?.id);
    const isAI = gameState?.aiPlayer === myRole;
    const isMyTurn = gameState?.currentRole === myRole;
    const boardSize = gameState?.board.length;

    const {
        selectedCell,
        movePath,
        handleCellClick,
        canConfirmMove,
        setSelectedCell,
        setMovePath
    } = useMakeMoveSelection(isMyTurn, gameState?.status, myRole, gameState?.board);

    const {confirmMove, isSubmitting} = useMakeMove(id, profile?.id)

    const handleConfirmMove = () => {
        confirmMove(movePath, () => {
            setSelectedCell(null);
            setMovePath([]);
        });
    }

    if (profileLoading || (id && gameLoading)) {
        return (
            <Page>
                <LoadingState/>
            </Page>
        );
    }

    if (profileError || !profile) {
        return (
            <Page>
                <ErrorState msg="Could not load your profile"/>
            </Page>
        );
    }

    if (!id) {
        return (
            <Page>
                <ErrorState msg="No game ID provided. Please join a game through the lobby."/>;
            </Page>
        );
    }

    if (gameError || !gameState) {
        return (
            <Page>
                <ErrorState msg={`Could not load game ${id}`}/>;
            </Page>
        );
    }

    return (
        <Page>
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
                                <ConfirmTurnButton
                                    onClick={handleConfirmMove}
                                    disabled={!canConfirmMove || isSubmitting}
                                >
                                    {isSubmitting ? "Making Move..." : "Confirm Move"}
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
                        disabled={!isMyTurn || isSubmitting}
                    />
                </div>

                <div className="mt-6 text-fg-2 text-sm text-center">
                    {isSubmitting
                        ? "Processing move..."
                        : !isMyTurn
                            ? "Waiting for opponent..."
                            : selectedCell
                                ? `Selected cell: ${selectedCell} - Click any cell to move`
                                : "Click a piece to select it"}
                </div>

                {gameState.status !== GameStatus.RUNNING && (
                    <GameEndModal
                        gameState={gameState}
                        myProfile={profile}
                        isOpen={true}
                    />
                )}
            </main>
        </Page>
    );
}