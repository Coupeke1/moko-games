import { useParams } from "react-router";
import { GameStatus } from "@/models/game-status";
import { useResetGame } from "@/hooks/use-reset-game";
import { useMyPlayerRole } from "@/hooks/use-my-player-role";
import { useMyProfile } from "@/hooks/use-my-profile";
import { useMakeMove } from "@/hooks/use-make-move";
import { useGameState } from "@/hooks/use-game-state";
import TurnIndicator from "@/components/turn-indicator.tsx";
import MyRoleDisplay from "@/components/my-role-display.tsx";
import LoadingState from "@/components/state/loading.tsx";
import ErrorState from "@/components/state/error.tsx";
import Page from "@/components/layout/page.tsx";
import GameGrid from "@/components/game-grid.tsx";
import Toast from "@/components/layout/Toast.tsx";
import GameStateDisplay from "@/components/game-state-display.tsx";
import GameEndModal from "@/components/dialogs/game-end-modal";
import PlayerCard from "@/components/player-card";

export default function GamePage() {
    const { id } = useParams<{ id: string }>()
    const { data: gameState, isLoading, isError } = useGameState(id!);
    const { profile, isLoading: profileLoading, isError: profileError } = useMyProfile();
    const myRole = useMyPlayerRole(gameState?.players, profile?.id)

    const { makeMove, errorMsg, closeToast } = useMakeMove(id!, profile, gameState?.status);
    const onReset = useResetGame(id!);

    if (isLoading || !gameState || profileLoading || !profile)
        return (
            <Page>
                <LoadingState />
            </Page>
        );
    if (isError)
        return (
            <Page>
                <ErrorState msg="Could not load the game" />
            </Page>
        );

    if (profileError) return (
        <Page>
            <ErrorState msg="Could not load your profile" />
        </Page>
    );

    return (
        <div className="flex flex-col items-center gap-8 p-8 min-h-screen bg-bg text-fg">
            <header className="text-center w-full">
                <h1 className="text-3xl font-bold mb-4">Tic Tac Toe</h1>
            </header>

            <div className="grid grid-1 md:grid-cols-2 gap-10 w-full justify-between items-center">
                <div className="grid grid-cols-2 md:grid-cols-1 gap-4">
                    {gameState.players.map(player => 
                        <PlayerCard
                            player={player}
                            currentTurn={gameState.currentRole === player.role} />
                    )}
                </div>

                <div className="flex flex-col items-center gap-6 flex-1">
                    <div className="game-board">
                        <GameGrid board={gameState.board} onCellClick={makeMove} />

                        {errorMsg && (
                            <Toast message={errorMsg} onClose={closeToast} />
                        )}
                    </div>
                </div>
            </div>

            {gameState.status !== GameStatus.IN_PROGRESS && (
                <GameEndModal
                    gameState={gameState}
                    myProfile={profile}
                    isOpen={true}
                    onReset={onReset}
                />
            )}


        </div>
    )
}