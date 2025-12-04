package be.kdg.team22.checkersservice.domain.move;

import be.kdg.team22.checkersservice.domain.board.Board;
import be.kdg.team22.checkersservice.domain.board.Piece;
import be.kdg.team22.checkersservice.domain.move.exceptions.InvalidMoveException;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

import java.util.Optional;

public class KingMoveValidator {
    protected static void validateKingMove(final Board board, final PlayerRole currentRole, final Move move) {

    }

    protected static boolean isKingCaptureMove(final Board board, final PlayerRole currentRole, final int rowDiff, final int[] fromCoords, final int[] toCoords) {
        if (rowDiff == 1) {
            return false;
        } else if (rowDiff >= 2) {
            int[][] cellsBetween = board.cellsBetween(fromCoords, toCoords);

            int opponentPieceCount = 0;
            int lastCellWithPieceIndex = -1;
            for (int i = 0; i < cellsBetween.length; i++) {
                int[] cell = cellsBetween[i];
                int cellNumber = board.convertCoordinatesToCellNumber(cell[0], cell[1]);
                Optional<Piece> cellPiece = board.pieceAt(cellNumber);

                if (cellPiece.isPresent()) {
                    if (cellPiece.get().color().equals(currentRole)) {
                        throw new InvalidMoveException("There is a piece of your own in the way");
                    } else {
                        opponentPieceCount++;
                        lastCellWithPieceIndex = i;
                    }
                }
            }

            if (opponentPieceCount == 1) {
                if (lastCellWithPieceIndex != cellsBetween.length - 1) {
                    throw new InvalidMoveException("Opponent piece must be in the last occupied cell in the path");
                } else {
                    return true;
                }
            } else if (opponentPieceCount == 0) {
                return false;
            } else {
                throw new InvalidMoveException("You cannot capture multiple pieces in one row");
            }
        } else {
            throw new InvalidMoveException("You cannot move to the same space");
        }
    }
}