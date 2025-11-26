import {useParams} from "react-router";
import {useGameState} from "@/routes/game/hooks/use-game-state.ts";
import {TurnIndicator} from "@/routes/game/components/turn-indicator.tsx";
import {MyRoleDisplay} from "@/routes/game/components/my-role-display.tsx";
import {useMyPlayerRole} from "@/routes/game/hooks/use-my-player-role.ts";
import {useMyProfile} from "@/routes/game/hooks/use-my-profile.ts";
import LoadingState from "@/components/state/loading.tsx";
import ErrorState from "@/components/state/error.tsx";
import Page from "@/components/layout/page.tsx";
import {Row} from "@/components/layout/row.tsx";
import {GameGrid} from "@/routes/game/components/game-grid.tsx";
import {useMakeMove} from "@/routes/game/hooks/use-make-move.ts";
import {Toast} from "@/components/layout/Toast.tsx";

export default function GamePage() {
    const {id} = useParams<{ id: string }>()
    const {data: gameState, isLoading, isError} = useGameState(id!);
    const {profile, isLoading: profileLoading, isError: profileError} = useMyProfile();
    const myRole = useMyPlayerRole(gameState?.players, profile?.id)

    const { makeMove, errorMsg, closeToast } = useMakeMove(id!, profile);

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
                <div className="flex justify-between items-center mb-4">
                    <h1 className="text-3xl font-bold flex-1 text-center">Tic Tac Toe</h1>
                </div>
            </header>

            <MyRoleDisplay
                userId={profile.id || 'Unknown'}
                role={myRole}/>

            <div className="game-board">
                <h3 className="text-xl font-semibold mb-4 text-center">Board</h3>
                <GameGrid board={gameState.board} onCellClick={makeMove} />

                {errorMsg && (
                    <Toast message={errorMsg} onClose={closeToast} />
                )}
            </div>

            <div className="grid grid-cols-2 gap-4 items-stretch">
                <TurnIndicator gameState={gameState}/>
                <section className="game-info bg-bg-2 p-4 text-center rounded-lg
                    flex flex-col justify-center gap-2">
                    <Row label="Game Status" value={gameState.status}/>
                    <Row label="Players" value={gameState.players.length}/>
                    {gameState.winner && <Row label="Winner" value={gameState.winner}/>}
                </section>
            </div>
        </div>
    )
}