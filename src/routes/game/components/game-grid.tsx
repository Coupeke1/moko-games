interface GameGridProps {
    board: string[][];
    onCellClick?: (rowIndex: number, colIndex: number) => void;
}

export function GameGrid({board, onCellClick}: GameGridProps) {
    const size = board.length;

    return (
        <div
            className="grid gap-1 p-2 bg-bg-3 rounded-xl shadow-md"
            style={{
                gridTemplateColumns: `repeat(${size}, 1fr)`,
                gridTemplateRows: `repeat(${size}, 1fr)`,
                width: '32rem',
                height: '32rem',
            }}
        >
            {board.map((row, rowIndex) =>
                row.map((cell, colIndex) => (
                    <button
                        key={`${rowIndex}-${colIndex}`}
                        className="aspect-square flex items-center justify-center
                       text-3xl font-bold bg-bg-2 hover:bg-bg-3 transition-colors"
                        onClick={() => onCellClick?.(rowIndex, colIndex)}
                    >
                        {cell || ''}
                    </button>
                ))
            )}
        </div>
    );
}
