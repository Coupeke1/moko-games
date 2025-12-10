import { GameGrid } from "@/routes/game/components/game-grid.tsx";
import { useState } from "react";

// Demo board for testing - standard checkers starting position
const createDemoBoard = (size: number = 8): string[][] => {
    const board: string[][] = [];

    for (let row = 0; row < size; row++) {
        const rowArray: string[] = [];
        for (let col = 0; col < size; col++) {
            const isPlayable = (row + col) % 2 === 1;

            if (!isPlayable) {
                rowArray.push("  "); // Light square - empty
            } else if (row < 3) {
                rowArray.push("W "); // White pieces at top
            } else if (row >= size - 3) {
                rowArray.push("B "); // Black pieces at bottom
            } else {
                rowArray.push("  "); // Empty playable square
            }
        }
        board.push(rowArray);
    }

    return board;
};

export default function GamePage() {
    const [board] = useState(() => createDemoBoard(8));
    const [selectedCell, setSelectedCell] = useState<number | null>(null);

    const handleCellClick = (cellIndex: number) => {
        if (selectedCell === cellIndex) {
            setSelectedCell(null); // Deselect if clicking same cell
        } else {
            setSelectedCell(cellIndex);
        }
        console.log(`Cell clicked: ${cellIndex}`);
    };

    return (
        <div className="min-h-screen bg-bg flex flex-col items-center justify-center p-4">
            <h1 className="text-fg text-3xl font-bold mb-6">Checkers</h1>

            <GameGrid
                board={board}
                selectedCell={selectedCell}
                validMoves={[]}
                movePath={[]}
                onCellClick={handleCellClick}
            />

            <div className="mt-4 text-fg-2">
                {selectedCell ? `Selected cell: ${selectedCell}` : "Click a piece to select it"}
            </div>
        </div>
    );
}