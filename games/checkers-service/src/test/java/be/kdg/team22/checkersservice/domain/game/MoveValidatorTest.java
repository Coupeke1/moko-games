package be.kdg.team22.checkersservice.domain.game;

import be.kdg.team22.checkersservice.domain.board.Board;
import be.kdg.team22.checkersservice.domain.game.exceptions.OutsidePlayingFieldException;
import be.kdg.team22.checkersservice.domain.move.KingMovementMode;
import be.kdg.team22.checkersservice.domain.move.Move;
import be.kdg.team22.checkersservice.domain.move.MoveValidator;
import be.kdg.team22.checkersservice.domain.board.Piece;
import be.kdg.team22.checkersservice.domain.move.exceptions.*;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MoveValidatorTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = Board.create(8);
    }

    @Test
    void moveShouldAllowValidForwardMoveForWhite() {
        Move move = new Move(null, List.of(9, 13));

        assertDoesNotThrow(() ->
                MoveValidator.move(board, PlayerRole.WHITE, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void moveShouldAllowValidForwardMoveForBlack() {
        Move move = new Move(null, List.of(24, 20));

        assertDoesNotThrow(() ->
                MoveValidator.move(board, PlayerRole.BLACK, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void moveShouldThrowWhenMovingEmptyCell() {
        Move move = new Move(null, List.of(16, 20));

        assertThrows(StartingPieceNotFoundException.class, () ->
                MoveValidator.move(board, PlayerRole.WHITE, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void moveShouldThrowWhenMovingOpponentPiece() {
        Move move = new Move(null, List.of(9, 13));

        assertThrows(NotPlayersPieceException.class, () ->
                MoveValidator.move(board, PlayerRole.BLACK, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void moveShouldThrowWhenMovingBackwardsAsWhite() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(10, new Piece(PlayerRole.WHITE, false));
        testBoard.grid().put(18, new Piece(PlayerRole.BLACK, false));
        Move move = new Move(null, List.of(10, 6));

        assertThrows(BackwardsMoveException.class, () ->
                MoveValidator.move(testBoard, PlayerRole.WHITE, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void moveShouldThrowWhenMovingBackwardsAsBlack() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(10, new Piece(PlayerRole.WHITE, false));
        testBoard.grid().put(18, new Piece(PlayerRole.BLACK, false));
        Move move = new Move(null, List.of(18, 23));

        assertThrows(BackwardsMoveException.class, () ->
                MoveValidator.move(testBoard, PlayerRole.BLACK, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void moveShouldThrowWhenTargetCellOccupied() {
        Board testBoard = createTestBoard();
        Move move = new Move(null, List.of(9, 13));

        assertThrows(TargetCellNotEmptyException.class, () ->
                MoveValidator.move(testBoard, PlayerRole.WHITE, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void moveShouldThrowWhenNotDiagonal() {
        Board testBoard = createTestBoard();
        Move move = new Move(null, List.of(9, 10));

        assertThrows(MoveNotDiagonalException.class, () ->
                MoveValidator.move(testBoard, PlayerRole.WHITE, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void moveShouldThrowWhenMovingMoreThanOneStepWithoutCapture() {
        Move move = new Move(null, List.of(9, 18));

        assertThrows(TooManyTilesException.class, () ->
                MoveValidator.move(board, PlayerRole.WHITE, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void moveShouldAllowValidCapture() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(9, new Piece(PlayerRole.WHITE, false));
        testBoard.grid().put(14, new Piece(PlayerRole.BLACK, false));
        Move move = new Move(null, List.of(9, 18));

        assertDoesNotThrow(() ->
                MoveValidator.move(testBoard, PlayerRole.WHITE, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void moveShouldThrowWhenCapturingOwnPiece() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(9, new Piece(PlayerRole.WHITE, false));
        testBoard.grid().put(14, new Piece(PlayerRole.WHITE, false));
        Move move = new Move(null, List.of(9, 18));

        assertThrows(OwnPieceInTheWayException.class, () ->
                MoveValidator.move(testBoard, PlayerRole.WHITE, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void moveShouldThrowWhenCellOutsideBoard() {
        Move move = new Move(null, List.of(1, 33));

        assertThrows(OutsidePlayingFieldException.class, () ->
                MoveValidator.move(board, PlayerRole.WHITE, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void isCaptureMoveShouldReturnFalseForOneStepMove() {
        Move move = new Move(null, List.of(9, 13));
        Optional<Piece> testPiece = board.pieceAt(9);

        assert testPiece.isPresent();
        assertFalse(MoveValidator.isCaptureMove(board, testPiece.get().color(), move));
    }

    @Test
    void validateNormalMoveShouldAllowPromotionMoveForWhite() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(25, new Piece(PlayerRole.WHITE, false));
        Move move = new Move(null, List.of(25, 29));

        assertDoesNotThrow(() ->
                MoveValidator.move(testBoard, PlayerRole.WHITE, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void validateNormalMoveShouldAllowPromotionMoveForBlack() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(5, new Piece(PlayerRole.BLACK, false));
        Move move = new Move(null, List.of(5, 1));

        assertDoesNotThrow(() ->
                MoveValidator.move(testBoard, PlayerRole.BLACK, move, KingMovementMode.FLYING)
        );
    }

    @Test
    void moveShouldAllowTwoStepKingCaptureMove() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(9, new Piece(PlayerRole.WHITE, true));
        testBoard.grid().put(18, new Piece(PlayerRole.BLACK, false));
        Move move = new Move(null, List.of(9, 23));

        assertDoesNotThrow(() ->
                MoveValidator.move(testBoard, PlayerRole.WHITE, move, KingMovementMode.DOUBLE)
        );
    }

    private Board createTestBoard() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(9, new Piece(PlayerRole.WHITE, false));
        testBoard.grid().put(13, new Piece(PlayerRole.WHITE, false));

        return testBoard;
    }

    private void clearBoard(Board board) {
        int totalCells = (board.size() * board.size()) / 2;
        for (int i = 1; i <= totalCells; i++) {
            board.grid().put(i, null);
        }
    }
}