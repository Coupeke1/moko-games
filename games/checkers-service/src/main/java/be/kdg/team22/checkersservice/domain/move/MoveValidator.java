package be.kdg.team22.checkersservice.domain.move;

import be.kdg.team22.checkersservice.domain.board.Board;
import be.kdg.team22.checkersservice.domain.board.Piece;
import be.kdg.team22.checkersservice.domain.game.exceptions.OutsidePlayingFieldException;
import be.kdg.team22.checkersservice.domain.move.exceptions.*;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.kdg.team22.checkersservice.domain.move.KingMoveValidator.isKingCaptureMove;
import static be.kdg.team22.checkersservice.domain.move.KingMoveValidator.validateKingMove;
import static be.kdg.team22.checkersservice.domain.move.NormalMoveValidator.isNormalCaptureMove;
import static be.kdg.team22.checkersservice.domain.move.NormalMoveValidator.validateNormalMove;

public class MoveValidator {
    private MoveValidator() {
    }

    public static MoveResult move(final Board board, final PlayerRole currentRole, final Move move, final KingMovementMode kingMovementMode) {
        Board tempBoard = new Board(board);
        int captureCount = 0;
        List<Move> successfulSegments = new ArrayList<>();
        for (int[] segment : move.segments()) {
            Move segmentMove = new Move(move.playerId(), List.of(segment[0], segment[1]));
            validateMove(tempBoard, currentRole, segmentMove, kingMovementMode);
            if (isCaptureMove(tempBoard, currentRole, segmentMove)) captureCount++;
            tempBoard.move(segmentMove);
            successfulSegments.add(segmentMove);
        }

        if (move.segments().size() > 1 && move.segments().size() != captureCount) {
            throw new TooManyMovesException();
        }

        boolean overallPromotion = false;
        int captures = 0;
        int kingCount = 0;
        for (Move segmentMove : successfulSegments) {
            MoveResult result = board.move(segmentMove);
            if (result.capture()) {
                captures++;
            }
            if (result.promotion()) {
                overallPromotion = true;
            }
            kingCount = result.kingCount();
        }

        return new MoveResult(captures > 0, captures > 1, overallPromotion, kingCount);
    }

    public static void validateMove(final Board board, final PlayerRole currentRole, final Move move, final KingMovementMode kingMovementMode) {
        for (int cell : move.cells()) {
            if (cell < 1 || cell > (board.size() * board.size()) / 2) {
                throw new OutsidePlayingFieldException();
            }
        }

        Optional<Piece> movingPiece = board.pieceAt(move.fromCell());
        if (movingPiece.isEmpty()) {
            throw new StartingPieceNotFoundException(move.fromCell());
        }

        if (movingPiece.get().color() != currentRole) {
            throw new NotPlayersPieceException(move.fromCell());
        }

        boolean isKing = movingPiece.get().isKing();
        if (isKing) {
            validateKingMove(board, currentRole, move, kingMovementMode);
        } else {
            validateNormalMove(board, currentRole, move);
        }
    }

    protected static void validateCellsWithinBoard(final Board board, final Move move) {
        int maxCells = (board.size() * board.size()) / 2;
        if (move.fromCell() > maxCells || move.cells().get(1) > maxCells || move.fromCell() <= 0 || move.cells().get(1) <= 0) {
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
        Optional<Piece> targetPiece = board.pieceAt(move.cells().get(1));
        if (targetPiece.isPresent()) {
            throw new TargetCellNotEmptyException(move.cells().get(1));
        }
    }

    protected static void validateDiagonalMove(final Board board, final Move move) {
        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.cells().get(1));

        int rowDiff = Math.abs(toCoords[0] - fromCoords[0]);
        int colDiff = Math.abs(toCoords[1] - fromCoords[1]);

        if (rowDiff != colDiff) {
            throw new MoveNotDiagonalException();
        }
    }

    protected static void validateForwardMove(final Board board, final PlayerRole currentRole, final Move move) {
        int[] fromCoords = board.convertCellNumberToCoordinates(move.fromCell());
        int[] toCoords = board.convertCellNumberToCoordinates(move.cells().get(1));

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
        int[] toCoords = board.convertCellNumberToCoordinates(move.cells().get(1));

        int rowDiff = Math.abs(toCoords[0] - fromCoords[0]);
        if (!piece.isKing()) {
            return isNormalCaptureMove(board, currentRole, rowDiff, fromCoords, toCoords);
        } else {
            return isKingCaptureMove(board, currentRole, rowDiff, fromCoords, toCoords);
        }
    }
}