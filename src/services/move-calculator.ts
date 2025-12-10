import {type GameState} from "@/models/game-state";
import {KingMovementMode} from "@/models/king-movement-mode";

export interface ValidMove {
    cells: number[];
    isCapture: boolean;
}

export function calculateValidMoves(
    gameState: GameState,
    fromCell: number
): ValidMove[] {
    const {board, currentRole, kingMovementMode} = gameState;
    const boardSize = board.length;

    const piece = getPieceAtCell(board, fromCell);
    if (!piece || !isPieceOwnedByPlayer(piece, currentRole)) {
        return [];
    }

    const isKing = piece.toUpperCase() === piece;
    const captureMoves = calculateCaptureMoves(board, fromCell, piece, isKing, kingMovementMode, boardSize);
    if (captureMoves.length > 0) {
        return captureMoves;
    }

    return calculateRegularMoves(board, fromCell, piece, isKing, kingMovementMode, boardSize);
}

function getPieceAtCell(board: string[][], cell: number): string | null {
    const boardSize = board.length;
    const row = Math.floor((cell - 1) / boardSize);
    const col = (cell - 1) % boardSize;

    if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
        return null;
    }

    const piece = board[row][col];
    return piece === "" ? null : piece;
}

function isPieceOwnedByPlayer(piece: string, role: string): boolean {
    if (role === "WHITE") {
        return piece === "w" || piece === "W";
    } else {
        return piece === "b" || piece === "B";
    }
}

function calculateCaptureMoves(
    board: string[][],
    fromCell: number,
    piece: string,
    isKing: boolean,
    kingMovementMode: KingMovementMode,
    boardSize: number
): ValidMove[] {
    const captures: ValidMove[] = [];

    // For kings with FLYING mode, calculate long-distance captures
    if (isKing && kingMovementMode === KingMovementMode.FLYING) {
        captures.push(...calculateFlyingCaptures(board, fromCell, piece, boardSize));
    } else {
        // Regular capture logic (one or two squares depending on mode)
        const maxDistance = isKing && kingMovementMode === KingMovementMode.DOUBLE ? 2 : 1;
        captures.push(...calculateJumpCaptures(board, fromCell, piece, isKing, maxDistance, boardSize));
    }

    return captures;
}

function calculateRegularMoves(
    board: string[][],
    fromCell: number,
    piece: string,
    isKing: boolean,
    kingMovementMode: KingMovementMode,
    boardSize: number
): ValidMove[] {
    const moves: ValidMove[] = [];
    const directions = getDirections(piece, isKing);

    for (const [dRow, dCol] of directions) {
        if (isKing && kingMovementMode === KingMovementMode.FLYING) {
            // Flying kings can move any distance
            moves.push(...calculateFlyingMoves(board, fromCell, dRow, dCol, boardSize));
        } else {
            const maxDistance = isKing && kingMovementMode === KingMovementMode.DOUBLE ? 2 : 1;
            // Regular moves (1 or 2 squares)
            for (let dist = 1; dist <= maxDistance; dist++) {
                const targetCell = getAdjacentCell(fromCell, dRow * dist, dCol * dist, boardSize);
                if (targetCell && getPieceAtCell(board, targetCell) === null) {
                    moves.push({cells: [fromCell, targetCell], isCapture: false});
                } else {
                    break; // Stop if blocked
                }
            }
        }
    }

    return moves;
}

function calculateJumpCaptures(
    board: string[][],
    fromCell: number,
    piece: string,
    isKing: boolean,
    maxDistance: number,
    boardSize: number
): ValidMove[] {
    const captures: ValidMove[] = [];
    const directions = getDirections(piece, isKing);
    const isWhite = piece.toLowerCase() === 'w';

    for (const [dRow, dCol] of directions) {
        for (let dist = 1; dist <= maxDistance; dist++) {
            const jumpedCell = getAdjacentCell(fromCell, dRow * dist, dCol * dist, boardSize);
            const landingCell = getAdjacentCell(fromCell, dRow * (dist + 1), dCol * (dist + 1), boardSize);

            if (!jumpedCell || !landingCell) continue;

            const jumpedPiece = getPieceAtCell(board, jumpedCell);
            const landingPiece = getPieceAtCell(board, landingCell);

            // Check if we're jumping over an opponent's piece to an empty cell
            if (jumpedPiece && isOpponentPiece(jumpedPiece, isWhite) && landingPiece === null) {
                const capturePath = [fromCell, landingCell];
                captures.push({cells: capturePath, isCapture: true});

                // Check for multi-captures recursively
                const multiCaptures = findMultiCaptures(board, landingCell, piece, isKing, maxDistance, boardSize, [jumpedCell]);
                for (const multiCapture of multiCaptures) {
                    captures.push({
                        cells: [fromCell, ...multiCapture.cells.slice(1)],
                        isCapture: true
                    });
                }
            }
        }
    }

    return captures;
}

function calculateFlyingMoves(
    board: string[][],
    fromCell: number,
    dRow: number,
    dCol: number,
    boardSize: number
): ValidMove[] {
    const moves: ValidMove[] = [];
    let distance = 1;

    while (true) {
        const targetCell = getAdjacentCell(fromCell, dRow * distance, dCol * distance, boardSize);
        if (!targetCell) break;

        const targetPiece = getPieceAtCell(board, targetCell);
        if (targetPiece !== null) break;

        moves.push({cells: [fromCell, targetCell], isCapture: false});
        distance++;
    }

    return moves;
}

function calculateFlyingCaptures(
    board: string[][],
    fromCell: number,
    piece: string,
    boardSize: number
): ValidMove[] {
    const captures: ValidMove[] = [];
    const directions = [[1, 1], [1, -1], [-1, 1], [-1, -1]];
    const isWhite = piece.toLowerCase() === 'w';

    for (const [dRow, dCol] of directions) {
        let distance = 1;
        let foundOpponent = false;

        while (true) {
            const targetCell = getAdjacentCell(fromCell, dRow * distance, dCol * distance, boardSize);
            if (!targetCell) break;

            const targetPiece = getPieceAtCell(board, targetCell);

            if (targetPiece === null) {
                if (foundOpponent) {
                    captures.push({cells: [fromCell, targetCell], isCapture: true});
                }
            } else if (!foundOpponent && isOpponentPiece(targetPiece, isWhite)) {
                foundOpponent = true;
            } else {
                break;
            }

            distance++;
        }
    }

    return captures;
}

function findMultiCaptures(
    board: string[][],
    fromCell: number,
    piece: string,
    isKing: boolean,
    maxDistance: number,
    boardSize: number,
    capturedCells: number[]
): ValidMove[] {
    const multiCaptures: ValidMove[] = [];
    const directions = getDirections(piece, isKing);
    const isWhite = piece.toLowerCase() === 'w';

    for (const [dRow, dCol] of directions) {
        for (let dist = 1; dist <= maxDistance; dist++) {
            const jumpedCell = getAdjacentCell(fromCell, dRow * dist, dCol * dist, boardSize);
            const landingCell = getAdjacentCell(fromCell, dRow * (dist + 1), dCol * (dist + 1), boardSize);

            if (!jumpedCell || !landingCell) continue;
            if (capturedCells.includes(jumpedCell)) continue; // Already captured

            const jumpedPiece = getPieceAtCell(board, jumpedCell);
            const landingPiece = getPieceAtCell(board, landingCell);

            if (jumpedPiece && isOpponentPiece(jumpedPiece, isWhite) && landingPiece === null) {
                multiCaptures.push({cells: [fromCell, landingCell], isCapture: true});

                // Continue recursively
                const furtherCaptures = findMultiCaptures(
                    board,
                    landingCell,
                    piece,
                    isKing,
                    maxDistance,
                    boardSize,
                    [...capturedCells, jumpedCell]
                );

                for (const furtherCapture of furtherCaptures) {
                    multiCaptures.push({
                        cells: [fromCell, ...furtherCapture.cells.slice(1)],
                        isCapture: true
                    });
                }
            }
        }
    }

    return multiCaptures;
}

function getDirections(piece: string, isKing: boolean): [number, number][] {
    if (isKing) {
        return [[1, 1], [1, -1], [-1, 1], [-1, -1]];
    }

    if (piece === 'w') {
        return [[-1, 1], [-1, -1]];
    } else {
        return [[1, 1], [1, -1]];
    }
}

function isOpponentPiece(piece: string, isWhite: boolean): boolean {
    const pieceIsWhite = piece.toLowerCase() === 'w';
    return pieceIsWhite !== isWhite;
}

function getAdjacentCell(cell: number, dRow: number, dCol: number, boardSize: number): number | null {
    const row = Math.floor((cell - 1) / boardSize);
    const col = (cell - 1) % boardSize;

    const newRow = row + dRow;
    const newCol = col + dCol;

    if (newRow < 0 || newRow >= boardSize || newCol < 0 || newCol >= boardSize) {
        return null;
    }

    return newRow * boardSize + newCol + 1;
}