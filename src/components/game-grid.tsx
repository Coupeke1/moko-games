import { isPlayableSquare, rowColToCellIndex, parseCellContent } from "@/lib/checkers-board.ts";

interface GameGridProps {
    board: string[][];
    selectedCell?: number | null;
    validMoves?: number[];
    movePath?: number[];
    onCellClick?: (cellIndex: number) => void;
    disabled?: boolean;
}

export function GameGrid({
    board,
    selectedCell,
    validMoves = [],
    movePath = [],
    disabled = false,
    onCellClick
}: GameGridProps) {
    const size = board.length;

    return (
        <div
            className="grid bg-bg-3 rounded-xl shadow-lg p-2 w-full max-w-[min(90vw,32rem)] aspect-square"
            style={{
                gridTemplateColumns: `repeat(${size}, 1fr)`,
                gridTemplateRows: `repeat(${size}, 1fr)`,
            }}
        >
            {board.map((row, rowIndex) =>
                row.map((cell, colIndex) => {
                    const isPlayable = isPlayableSquare(rowIndex, colIndex);
                    const cellIndex = isPlayable ? rowColToCellIndex(rowIndex, colIndex, size) : null;
                    const piece = parseCellContent(cell);

                    const isSelected = cellIndex !== null && cellIndex === selectedCell;
                    const isValidMove = cellIndex !== null && validMoves.includes(cellIndex);
                    const isInPath = cellIndex !== null && movePath.includes(cellIndex);

                    return (
                        <button
                            key={`${rowIndex}-${colIndex}`}
                            className={`
                                aspect-square flex items-center justify-center relative
                                transition-all duration-150
                                ${isPlayable ? 'bg-tile-dark' : 'bg-tile-light'}
                                ${isSelected ? 'ring-4 ring-selected ring-inset' : ''}
                                ${isPlayable && !piece ? 'cursor-pointer' : ''}
                                ${piece ? 'cursor-pointer' : ''}
                            `}
                            onClick={() => {
                                if (isPlayable && cellIndex !== null && !disabled) {
                                    onCellClick?.(cellIndex);
                                }
                            }}
                            disabled={!isPlayable}
                        >
                            {isValidMove && !piece && (
                                <div className="absolute w-1/3 h-1/3 rounded-full bg-valid-move opacity-70" />
                            )}

                            {isInPath && !isSelected && (
                                <div className="absolute inset-1 rounded-full border-4 border-selected opacity-50" />
                            )}

                            {piece && (
                                <CheckerPiece
                                    color={piece.color}
                                    isKing={piece.isKing}
                                    isSelected={isSelected}
                                    canCapture={isValidMove}
                                />
                            )}

                            {piece && isValidMove && (
                                <div className="absolute inset-1 rounded-full border-4 border-valid-move opacity-70" />
                            )}
                        </button>
                    );
                })
            )}
        </div>
    );
}

interface CheckerPieceProps {
    color: 'black' | 'white';
    isKing: boolean;
    isSelected?: boolean;
    canCapture?: boolean;
}

function CheckerPiece({ color, isKing, isSelected }: CheckerPieceProps) {
    const baseClasses = `
        w-[80%] h-[80%] rounded-full
        flex items-center justify-center
        transition-transform duration-150
        shadow-md
        ${isSelected ? 'scale-110 shadow-lg' : ''}
    `;

    const colorClasses = color === 'black'
        ? 'bg-piece-black border-2 border-piece-black-highlight'
        : 'bg-piece-white border-2 border-piece-white-highlight';

    return (
        <div className={`${baseClasses} ${colorClasses}`}>
            {isKing && (
                <span className="text-piece-crown text-lg font-bold select-none">
                    ♔
                </span>
            )}
        </div>
    );
}