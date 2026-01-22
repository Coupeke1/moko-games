import {GameStatus} from "@/models/game-status.ts";
import {useCallback, useState} from "react";
import {getPieceAtCell, isPieceOwnedByPlayer} from "@/services/move-calculator.ts";

export function useMakeMoveSelection(isMyTurn: boolean, gameStatus: GameStatus | undefined, myRole: string | null, board: string[][] | undefined) {
    const [selectedCell, setSelectedCell] = useState<number | null>(null);
    const [movePath, setMovePath] = useState<number[]>([]);

    const handleCellClick = useCallback((cellIndex: number) => {
        if (!isMyTurn || gameStatus !== GameStatus.RUNNING || !board || !myRole) {
            console.log('Player cannot make a move right now');
            return;
        }

        if (!selectedCell) {
            const piece = getPieceAtCell(board, cellIndex);

            if (piece && isPieceOwnedByPlayer(piece, myRole)) {
                setSelectedCell(cellIndex);
                setMovePath([cellIndex]);
            }
            return;
        }

        if (cellIndex === selectedCell && movePath.length === 1) {
            setSelectedCell(null);
            setMovePath([]);
            return;
        }

        if (movePath.length > 1 && cellIndex === movePath[movePath.length - 1]) {
            const newPath = movePath.slice(0, -1);
            setMovePath(newPath);
            return;
        }

        const clickedIndexInPath = movePath.indexOf(cellIndex);
        if (clickedIndexInPath !== -1) {
            const newPath = movePath.slice(0, clickedIndexInPath + 1);
            setMovePath(newPath);
            return;
        }

        setMovePath([...movePath, cellIndex]);
    }, [isMyTurn, gameStatus, board, myRole, selectedCell, movePath]);

    const canConfirmMove = movePath.length >= 2;

    return {
        selectedCell,
        movePath,
        handleCellClick,
        canConfirmMove,
        setSelectedCell,
        setMovePath
    };
}