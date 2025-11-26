import {useParams} from "react-router";
import {useGameState} from "@/routes/game/hooks/use-game-state.ts";
import {TurnIndicator} from "@/routes/game/components/turn-indicator.tsx";
import {MyRoleDisplay} from "@/routes/game/components/my-role-display.tsx";
import {useMyPlayerRole} from "@/routes/game/hooks/use-my-player-role.ts";
import {useMyProfile} from "@/routes/game/hooks/use-my-profile.ts";
import LoadingState from "@/components/state/loading.tsx";
import ErrorState from "@/components/state/error.tsx";
import Page from "@/components/layout/page.tsx";

export default function GamePage() {
    const {id} = useParams<{ id: string }>()
    const {data: gameState, isLoading, isError} = useGameState(id!);
    const {profile, isLoading: profileLoading, isError: profileError} = useMyProfile();
    const myRole = useMyPlayerRole(gameState?.players, profile?.id)

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
                    <MyRoleDisplay
                        userId={profile?.id || 'Unknown'}
                        role={myRole}/>

                    <h1 className="text-3xl font-bold flex-1 text-center">Tic Tac Toe - Game #{id}</h1>

                    <div className="w-40"></div>
                </div>
            </header>

            <TurnIndicator gameState={gameState}/>

            <div className="game-board">
                <h3 className="text-xl font-semibold mb-4 text-center">Board</h3>
                <div className="border-2 border-fg rounded">
                    {gameState.board.map((row, rowIndex) => (
                        <div key={rowIndex} className="flex">
                            {row.map((cell, cellIndex) => (
                                <div
                                    key={cellIndex}
                                    className="w-16 h-16 border border-fg-2 flex items-center justify-center text-2xl font-bold bg-bg-2"
                                >
                                    {cell || ' '}
                                </div>
                            ))}
                        </div>
                    ))}
                </div>
            </div>

            <div className="game-info bg-bg-2 p-4 rounded-lg">
                <p><strong>Status:</strong> <span>{gameState.status}</span></p>
                <p><strong>Players:</strong> <span>{gameState.players.length}</span></p>
                {gameState.winner && (
                    <p><strong>Winner:</strong> <span>{gameState.winner}</span></p>
                )}
            </div>
        </div>
    )
}