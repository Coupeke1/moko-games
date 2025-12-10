export function isPlayableSquare(row: number, col: number): boolean {
    return (row + col) % 2 === 1;
}

export function rowColToCellIndex(row: number, col: number, boardSize: number = 8): number | null {
    if (!isPlayableSquare(row, col)) {
        return null;
    }

    const cellsPerRow = boardSize / 2;
    return row * cellsPerRow + Math.floor(col / 2) + 1;
}

export function cellIndexToRowCol(cellIndex: number, boardSize: number = 8): { row: number; col: number } {
    const cellsPerRow = boardSize / 2;
    const zeroBasedIndex = cellIndex - 1;

    const row = Math.floor(zeroBasedIndex / cellsPerRow);
    const positionInRow = zeroBasedIndex % cellsPerRow;
    const col = row % 2 === 0
        ? positionInRow * 2 + 1
        : positionInRow * 2;

    return { row, col };
}

export function getTotalCells(boardSize: number = 8): number {
    return (boardSize * boardSize) / 2;
}

export function parseCellContent(content: string): {
    color: 'black' | 'white';
    isKing: boolean
} | null {
    if (!content || content.trim() === '') {
        return null;
    }

    const trimmed = content.trim();

    if (trimmed === 'B' || content === 'B ') {
        return { color: 'black', isKing: false };
    }
    if (trimmed === 'BK') {
        return { color: 'black', isKing: true };
    }
    if (trimmed === 'W' || content === 'W ') {
        return { color: 'white', isKing: false };
    }
    if (trimmed === 'WK') {
        return { color: 'white', isKing: true };
    }

    return null;
}