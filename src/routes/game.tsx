import { useParams } from "react-router";
import { GameStatus } from "@/models/game-status";
import { useMyProfile } from "@/hooks/use-my-profile";
import { useMakeMove } from "@/hooks/use-make-move";
import { useGameState } from "@/hooks/use-game-state";
import LoadingState from "@/components/state/loading.tsx";
import ErrorState from "@/components/state/error.tsx";
import Page from "@/components/layout/page.tsx";
import GameGrid from "@/components/game-grid.tsx";
import GameEndModal from "@/components/dialogs/game-end-modal";
import PlayerCard from "@/components/player-card";
import TurnIndicator from "@/components/turn-indicator";
import {BigButton} from "@/components/big-button.tsx";
import {useBotMove} from "@/hooks/use-bot-move.ts";

export default function GamePage() {
    const { id } = useParams<{ id: string }>()
    const { data: gameState, isLoading, isError } = useGameState(id!);
    const { profile, isLoading: profileLoading, isError: profileError } = useMyProfile();
    const isMyTurn = gameState?.currentRole === gameState?.players.find(p => p.id === profile?.id)?.role;

    const { makeMove } = useMakeMove(id!, profile, gameState?.status);
    const {requestBotTurn, isBotMoving} = useBotMove(id);

    const handleBotMove = () => {
        requestBotTurn();
    }

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
        <Page>
            <div className="flex flex-col justify-start items-center gap-8 min-h-screen">
                <div className="grid grid-1 md:grid-cols-2 gap-10 justify-between items-center">
                    <div className="grid grid-cols-1 gap-4">
                        {gameState.players.map(player =>
                            <PlayerCard
                                player={player}
                                currentTurn={gameState.currentRole === player.role} />
                        )}
                    </div>

                    <div className="flex flex-col items-center gap-4 flex-1">
                        <TurnIndicator gameState={gameState}>
                            <BigButton
                                onClick={handleBotMove}
                                disabled={isBotMoving || isMyTurn}
                            >
                                {isBotMoving ? "Bot Thinking..." : "Request Bot Move"}
                            </BigButton>
                        </TurnIndicator>
                        <div className="game-board">
                            <GameGrid board={gameState.board} onCellClick={makeMove} />
                        </div>
                    </div>
                </div>

                {gameState.status !== GameStatus.IN_PROGRESS && (
                    <GameEndModal
                        gameState={gameState}
                        myProfile={profile}
                        isOpen={true}
                    />
                )}
            </div>
        </Page>
    )
}