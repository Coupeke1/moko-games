import {useParams} from "react-router";
import {useGameState} from "@/routes/game/hooks/useGameState.ts";
import {TurnIndicator} from "@/routes/game/components/TurnIndicator.tsx";

export default function GamePage() {
    const {id} = useParams<{ id: string }>()
    const { data: gameState, isLoading, isError, error } = useGameState(id!);

    if (isLoading) return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="text-fg-2 text-xl">Loading game {id}...</div>
        </div>
    )

    if (isError) return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="text-red-500 text-xl">
                Error loading game: {error instanceof Error ? error.message : 'Unknown error'}
            </div>
        </div>
    )

    if (!gameState) return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="text-fg-2 text-xl">No game data found</div>
        </div>
    )

    return (
        <div className="flex flex-col items-center gap-8 p-8 min-h-screen bg-bg text-fg">
            <header className="text-center">
                <h1 className="text-3xl font-bold">Tic Tac Toe - Game #{id}</h1>
            </header>

            <TurnIndicator gameState={gameState} />

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