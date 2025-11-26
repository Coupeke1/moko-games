import {useParams} from "react-router";
import {useGameState} from "@/routes/game/hooks/use-game-state.ts";
import {TurnIndicator} from "@/routes/game/components/turn-indicator.tsx";
import {MyRoleDisplay} from "@/routes/game/components/my-role-display.tsx";
import {useMyPlayerRole} from "@/routes/game/hooks/use-my-player-role.ts";
import {useMyProfile} from "@/routes/game/hooks/use-my-profile.ts";
import LoadingState from "@/components/state/loading.tsx";
import ErrorState from "@/components/state/error.tsx";
import Page from "@/components/layout/page.tsx";
import {GameGrid} from "@/routes/game/components/game-grid.tsx";
import {useMakeMove} from "@/routes/game/hooks/use-make-move.ts";
import {Toast} from "@/components/layout/Toast.tsx";
import {GameStateDisplay} from "@/routes/game/components/game-state-display.tsx";

export default function GamePage() {
    const {id} = useParams<{ id: string }>()
    const {data: gameState, isLoading, isError} = useGameState(id!);
    const {profile, isLoading: profileLoading, isError: profileError} = useMyProfile();
    const myRole = useMyPlayerRole(gameState?.players, profile?.id)

    const {makeMove, errorMsg, closeToast} = useMakeMove(id!, profile, gameState?.status);

    if (isLoading || !gameState || profileLoading || !profile)
        return (
            <Page>
                <LoadingState/>
            </Page>
        );
    if (isError)
        return (
            <Page>
                <ErrorState msg="Could not load the game"/>
            </Page>
        );

    if (profileError) return (
        <Page>
            <ErrorState msg="Could not load your profile"/>
        </Page>
    );

    return (
        <div className="flex flex-col items-center gap-8 p-8 min-h-screen bg-bg text-fg">
            <header className="text-center w-full">
                <h1 className="text-3xl font-bold mb-4">Tic Tac Toe</h1>
            </header>

            <div className="flex items-start justify-center gap-30 w-full max-w-6xl">
                <div className="w-80 flex-shrink-0 mt-2">
                    <MyRoleDisplay
                        profile={profile}
                        role={myRole}/>
                </div>

                <div className="flex flex-col items-center gap-6 flex-1 max-w-md">
                    <div className="game-board">
                        <GameGrid board={gameState.board} onCellClick={makeMove}/>

                        {errorMsg && (
                            <Toast message={errorMsg} onClose={closeToast}/>
                        )}
                    </div>
                </div>

                <div className="w-80 flex-shrink-0 flex flex-col gap-4 mt-2">
                    <TurnIndicator gameState={gameState}/>
                    <GameStateDisplay gameState={gameState}/>
                </div>
            </div>
        </div>
    )
}