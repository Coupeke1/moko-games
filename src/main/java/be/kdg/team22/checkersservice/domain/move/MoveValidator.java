package be.kdg.team22.checkersservice.domain.move;

import be.kdg.team22.checkersservice.domain.board.Board;
import be.kdg.team22.checkersservice.domain.board.Piece;
import be.kdg.team22.checkersservice.domain.move.exceptions.InvalidMoveException;
import be.kdg.team22.checkersservice.domain.game.exceptions.OutsidePlayingFieldException;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

import java.util.Optional;

import static be.kdg.team22.checkersservice.domain.move.KingMoveValidator.isKingCaptureMove;
import static be.kdg.team22.checkersservice.domain.move.KingMoveValidator.validateKingMove;
import static be.kdg.team22.checkersservice.domain.move.NormalMoveValidator.isNormalCaptureMove;
import static be.kdg.team22.checkersservice.domain.move.NormalMoveValidator.validateNormalMove;

public class MoveValidator {
    private MoveValidator() {
    }

    public static void validateMove(final Board board, final PlayerRole currentRole, final Move move) {
        Optional<Piece> movingPiece = board.pieceAt(move.fromCell());
        if (movingPiece.isPresent() && movingPiece.get().isKing()) {
            validateKingMove(board, currentRole, move);
        } else {
            validateNormalMove(board, currentRole, move);
        }
    }

    protected static void validateCellsWithinBoard(final Board board, final Move move) {
        int maxCells = (board.size() * board.size()) / 2;
        if (move.fromCell() > maxCells || move.toCell() > maxCells || move.fromCell() <= 0 || move.toCell() <= 0) {
            throw new InvalidMoveException("Cell numbers must be between 1 and " + maxCells);
        }
    }

    protected static Piece validateStartingPiece(final Board board, final PlayerRole currentRole, final Move move) {
        Optional<Piece> optionalPiece = board.pieceAt(move.fromCell());
        if (optionalPiece.isEmpty()) {
            throw new InvalidMoveException("No piece at starting cell " + move.fromCell());
        }

        Piece piece = optionalPiece.get();
        if (piece.color() != currentRole) {
            throw new InvalidMoveException("Piece at cell " + move.fromCell() + " does not belong to current player");
        }

        return piece;
    }

    protected static void validateTargetCellEmpty(final Board board, final Move move) {
        Optional<Piece> targetPiece = board.pieceAt(move.toCell());
        if (targetPiece.isPresent()) {
            throw new InvalidMoveException("Target cell " + move.toCell() + " is not empty");
        }
    }

    protected static void validateDiagonalMove(final Board board, final Move move) {
        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.toCell());

        int rowDiff = Math.abs(toCoords[0] - fromCoords[0]);
        int colDiff = Math.abs(toCoords[1] - fromCoords[1]);

        if (rowDiff != colDiff) {
            throw new InvalidMoveException("Move must be diagonal");
        }
    }

    protected static void validateForwardMove(final Board board, final PlayerRole currentRole, final Move move) {
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

    public static boolean isCaptureMove(final Board board, final PlayerRole currentRole, final Move move, final Piece piece) {
        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.toCell());

        int rowDiff = Math.abs(toCoords[0] - fromCoords[0]);
        if (!piece.isKing()) {
            return isNormalCaptureMove(board, currentRole, rowDiff, fromCoords, toCoords);
        } else {
            return isKingCaptureMove(board, currentRole, rowDiff, fromCoords, toCoords);
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