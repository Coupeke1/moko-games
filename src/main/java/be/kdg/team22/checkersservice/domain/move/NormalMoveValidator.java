package be.kdg.team22.checkersservice.domain.move;

import be.kdg.team22.checkersservice.domain.board.Board;
import be.kdg.team22.checkersservice.domain.board.Piece;
import be.kdg.team22.checkersservice.domain.move.exceptions.InvalidMoveException;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

import java.util.Optional;

import static be.kdg.team22.checkersservice.domain.move.MoveValidator.*;

public class NormalMoveValidator {
    protected static void validateNormalMove(final Board board, final PlayerRole currentRole, final Move move) {
        validateCellsWithinBoard(board, move);
        validateStartingPiece(board, currentRole, move);
        validateTargetCellEmpty(board, move);
        validateDiagonalMove(board, move);
        validateForwardMove(board, currentRole, move);

        if (!isCaptureMove(board, currentRole, move)) {
            validateSingleStep(board, move);
        }
    }

    protected static boolean isNormalCaptureMove(final Board board, final PlayerRole currentRole, final int rowDiff, final int[] fromCoords, final int[] toCoords) {
        if (rowDiff == 1) {
            return false;
        } else if (rowDiff == 2) {
            int[] middleCell = {(fromCoords[0] + toCoords[0]) / 2, (fromCoords[1] + toCoords[1]) / 2};
            Optional<Piece> capturedPiece = board.pieceAt(board.convertCoordinatesToCellNumber(middleCell[0], middleCell[1]));
            if (capturedPiece.isEmpty()) {
                throw new InvalidMoveException("You can only move 1 tile");
            } else if (capturedPiece.get().color().equals(currentRole)) {
                throw new InvalidMoveException("You can not capture your own pieces");
            } else {
                return true;
            }
        } else {
            throw new InvalidMoveException("You can only move 1 tile or capture over 2 tiles");
        }
    }

    private static void validateSingleStep(final Board board, final Move move) {
        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.toCell());

        int rowDiff = Math.abs(toCoords[0] - fromCoords[0]);
        if (rowDiff != 1) {
            throw new InvalidMoveException("Normal move must be exactly one step");
        }
    }
}