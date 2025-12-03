package be.kdg.team22.checkersservice.domain.board;

import be.kdg.team22.checkersservice.domain.board.exceptions.InvalidMoveException;
import be.kdg.team22.checkersservice.domain.game.exceptions.OutsidePlayingFieldException;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

import java.util.Optional;

public class MoveValidator {
    private MoveValidator() {
    }

    public static void validateNormalMove(final Board board, final PlayerRole currentRole, final Move move) {
        validateCellsWithinBoard(board, move);
        validateStartingPiece(board, currentRole, move);
        validateTargetCellEmpty(board, move);
        validateDiagonalMove(board, move);
        validateForwardMove(board, currentRole, move);

        if (isCaptureMove(board, move)) {
            validateCapture(board, currentRole, move);
        } else {
            validateSingleStepMove(board, move);
        }
    }

    private static void validateCellsWithinBoard(final Board board, final Move move) {
        int maxCells = (board.size() * board.size()) / 2;
        if (move.fromCell() > maxCells || move.toCell() > maxCells) {
            throw new InvalidMoveException("Cell numbers must be between 1 and " + maxCells);
        }
    }

    private static void validateStartingPiece(final Board board, final PlayerRole currentRole, final Move move) {
        Optional<Piece> startingPiece = board.pieceAt(move.fromCell());
        if (startingPiece.isEmpty()) {
            throw new InvalidMoveException("No piece at starting cell " + move.fromCell());
        }

        Piece piece = startingPiece.get();
        if (piece.color() != currentRole) {
            throw new InvalidMoveException("Piece at cell " + move.fromCell() + " does not belong to current player");
        }

        if (piece.isKing()) {
            throw new InvalidMoveException("Piece at cell " + move.fromCell() + " is a king, use king move validation");
        }
    }

    private static void validateTargetCellEmpty(final Board board, final Move move) {
        Optional<Piece> targetPiece = board.pieceAt(move.toCell());
        if (targetPiece.isPresent()) {
            throw new InvalidMoveException("Target cell " + move.toCell() + " is not empty");
        }
    }

    private static void validateDiagonalMove(final Board board, final Move move) {
        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.toCell());

        int rowDiff = Math.abs(toCoords[0] - fromCoords[0]);
        int colDiff = Math.abs(toCoords[1] - fromCoords[1]);

        if (rowDiff != colDiff) {
            throw new InvalidMoveException("Move must be diagonal");
        }
    }

    private static void validateForwardMove(final Board board, final PlayerRole currentRole, final Move move) {
        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.toCell());

        int rowDiff = toCoords[0] - fromCoords[0];

        if (currentRole == PlayerRole.WHITE && rowDiff <= 0) {
            throw new InvalidMoveException("White pieces must move forward (down)");
        }

        if (currentRole == PlayerRole.BLACK && rowDiff >= 0) {
            throw new InvalidMoveException("Black pieces must move forward (up)");
        }
    }

    private static boolean isCaptureMove(final Board board, final Move move) {
        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.toCell());

        int rowDiff = Math.abs(toCoords[0] - fromCoords[0]);
        return rowDiff == 2;
    }

    private static void validateSingleStepMove(final Board board, final Move move) {
        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.toCell());

        int rowDiff = Math.abs(toCoords[0] - fromCoords[0]);
        if (rowDiff != 1) {
            throw new InvalidMoveException("Normal move must be exactly one step");
        }
    }

    private static void validateCapture(final Board board, final PlayerRole currentRole, final Move move) {
        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.toCell());

        int middleRow = (fromCoords[0] + toCoords[0]) / 2;
        int middleCol = (fromCoords[1] + toCoords[1]) / 2;

        try {
            int middleCell = board.convertCoordinatesToCellNumber(middleRow, middleCol);
            Optional<Piece> middlePiece = board.pieceAt(middleCell);

            if (middlePiece.isEmpty()) {
                throw new InvalidMoveException("No piece to capture between cells " + move.fromCell() + " and " + move.toCell());
            }

            if (middlePiece.get().color() == currentRole) {
                throw new InvalidMoveException("Cannot capture your own piece");
            }

        } catch (OutsidePlayingFieldException e) {
            throw new InvalidMoveException(e.getMessage());
        }
    }
}
