package be.kdg.team22.checkersservice.domain.move;

import be.kdg.team22.checkersservice.domain.board.Board;
import be.kdg.team22.checkersservice.domain.board.Piece;
import be.kdg.team22.checkersservice.domain.game.exceptions.OutsidePlayingFieldException;
import be.kdg.team22.checkersservice.domain.move.exceptions.*;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

import java.util.Optional;

import static be.kdg.team22.checkersservice.domain.move.KingMoveValidator.isKingCaptureMove;
import static be.kdg.team22.checkersservice.domain.move.KingMoveValidator.validateKingMove;
import static be.kdg.team22.checkersservice.domain.move.NormalMoveValidator.isNormalCaptureMove;
import static be.kdg.team22.checkersservice.domain.move.NormalMoveValidator.validateNormalMove;

public class MoveValidator {
    private MoveValidator() {
    }

    public static void validateMove(final Board board, final PlayerRole currentRole, final Move move, KingMovementMode kingMovementMode) {
        Optional<Piece> movingPiece = board.pieceAt(move.fromCell());
        if (movingPiece.isPresent() && movingPiece.get().isKing()) {
            validateKingMove(board, currentRole, move, kingMovementMode);
        } else {
            validateNormalMove(board, currentRole, move);
        }
    }

    protected static void validateCellsWithinBoard(final Board board, final Move move) {
        int maxCells = (board.size() * board.size()) / 2;
        if (move.fromCell() > maxCells || move.toCell() > maxCells || move.fromCell() <= 0 || move.toCell() <= 0) {
            throw new OutsidePlayingFieldException();
        }
    }

    protected static void validateStartingPiece(final Board board, final PlayerRole currentRole, final Move move) {
        Optional<Piece> optionalPiece = board.pieceAt(move.fromCell());
        if (optionalPiece.isEmpty()) {
            throw new StartingPieceNotFoundException(move.fromCell());
        }

        Piece piece = optionalPiece.get();
        if (piece.color() != currentRole) {
            throw new NotPlayersPieceException(move.fromCell());
        }
    }

    protected static void validateTargetCellEmpty(final Board board, final Move move) {
        Optional<Piece> targetPiece = board.pieceAt(move.toCell());
        if (targetPiece.isPresent()) {
            throw new TargetCellNotEmptyException(move.toCell());
        }
    }

    protected static void validateDiagonalMove(final Board board, final Move move) {
        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.toCell());

        int rowDiff = Math.abs(toCoords[0] - fromCoords[0]);
        int colDiff = Math.abs(toCoords[1] - fromCoords[1]);

        if (rowDiff != colDiff) {
            throw new MoveNotDiagonalException();
        }
    }

    protected static void validateForwardMove(final Board board, final PlayerRole currentRole, final Move move) {
        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.toCell());

        int rowDiff = toCoords[0] - fromCoords[0];

        if (currentRole == PlayerRole.WHITE && rowDiff <= 0) {
            throw new BackwardsMoveException(currentRole);
        } else if (currentRole == PlayerRole.BLACK && rowDiff >= 0) {
            throw new BackwardsMoveException(currentRole);
        }
    }

    public static boolean isCaptureMove(final Board board, final PlayerRole currentRole, final Move move) {
        Optional<Piece> optionalPiece = board.pieceAt(move.fromCell());
        if (optionalPiece.isEmpty()) {
            throw new StartingPieceNotFoundException(move.fromCell());
        }
        Piece piece = optionalPiece.get();

        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.toCell());

        int rowDiff = Math.abs(toCoords[0] - fromCoords[0]);
        if (!piece.isKing()) {
            return isNormalCaptureMove(board, currentRole, rowDiff, fromCoords, toCoords);
        } else {
            return isKingCaptureMove(board, currentRole, rowDiff, fromCoords, toCoords);
        }
    }
}