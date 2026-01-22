import {cellIndexToRowCol} from "@/lib/checkers-board.ts";

export function getPieceAtCell(board: string[][], cell: number): string | null {
    if (!board) return null;
    const boardSize = board.length;
    const { row, col } = cellIndexToRowCol(cell, board.length);

    if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
        return null;
    }

    const raw = board[row][col];
    const piece = raw.trim();

    return piece === "" ? null : piece;
}

export function isPieceOwnedByPlayer(piece: string, role: string): boolean {
    if (!piece) return false;

    const trimmedPiece = piece.trim();
    if (role === "WHITE") {
        return trimmedPiece === "W" || trimmedPiece === "WK";
    } else {
        return trimmedPiece === "B" || trimmedPiece === "BK";
    }
}