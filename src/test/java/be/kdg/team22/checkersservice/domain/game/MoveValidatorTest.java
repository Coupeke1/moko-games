package be.kdg.team22.checkersservice.domain.game;

import be.kdg.team22.checkersservice.domain.board.Board;
import be.kdg.team22.checkersservice.domain.board.Move;
import be.kdg.team22.checkersservice.domain.board.MoveValidator;
import be.kdg.team22.checkersservice.domain.board.Piece;
import be.kdg.team22.checkersservice.domain.board.exceptions.InvalidMoveException;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveValidatorTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = Board.create(8);
    }

    @Test
    void validateMoveShouldAllowValidForwardMoveForWhite() {
        Move move = new Move(null, 9, 13);

        assertDoesNotThrow(() ->
                MoveValidator.validateMove(board, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateMoveShouldAllowValidForwardMoveForBlack() {
        Move move = new Move(null, 24, 20);

        assertDoesNotThrow(() ->
                MoveValidator.validateMove(board, PlayerRole.BLACK, move)
        );
    }

    @Test
    void validateMoveShouldThrowWhenMovingEmptyCell() {
        Move move = new Move(null, 16, 20);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateMove(board, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateMoveShouldThrowWhenMovingOpponentPiece() {
        Move move = new Move(null, 9, 13);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateMove(board, PlayerRole.BLACK, move)
        );
    }

    @Test
    void validateMoveShouldThrowWhenMovingBackwardsAsWhite() {
        Move move = new Move(null, 13, 9);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateMove(board, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateMoveShouldThrowWhenMovingBackwardsAsBlack() {
        Move move = new Move(null, 20, 24);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateMove(board, PlayerRole.BLACK, move)
        );
    }

    @Test
    void validateMoveShouldThrowWhenTargetCellOccupied() {
        Board testBoard = createTestBoard();
        Move move = new Move(null, 9, 13);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateMove(testBoard, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateMoveShouldThrowWhenNotDiagonal() {
        Board testBoard = createTestBoard();
        Move move = new Move(null, 9, 10);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateMove(testBoard, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateMoveShouldThrowWhenMovingMoreThanOneStepWithoutCapture() {
        Move move = new Move(null, 9, 17);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateMove(board, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateMoveShouldAllowValidCapture() {
        Board testBoard = createCaptureBoard();
        Move move = new Move(null, 9, 18);

        assertDoesNotThrow(() ->
                MoveValidator.validateMove(testBoard, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateMoveShouldThrowWhenNoPieceToCapture() {
        Board testBoard = createTestBoard();
        Move move = new Move(null, 9, 18);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateMove(testBoard, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateMoveShouldThrowWhenCapturingOwnPiece() {
        Board testBoard = createOwnPieceCaptureBoard();
        Move move = new Move(null, 9, 18);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateMove(testBoard, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateMoveShouldThrowWhenCellOutsideBoard() {
        Move move = new Move(null, 1, 33);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateMove(board, PlayerRole.WHITE, move)
        );
    }

    @Test
    void isCaptureMoveShouldReturnTrueForTwoStepMove() {
        Board testBoard = createTestBoard();
        Move move = new Move(null, 9, 18);

        assertTrue(MoveValidator.isCaptureMove(testBoard, move));
    }

    @Test
    void isCaptureMoveShouldReturnFalseForOneStepMove() {
        Move move = new Move(null, 9, 13);

        assertFalse(MoveValidator.isCaptureMove(board, move));
    }

    @Test
    void validateNormalMoveShouldAllowPromotionMoveForWhite() {
        Board testBoard = createPromotionBoardWhite();
        Move move = new Move(null, 25, 29);

        assertDoesNotThrow(() ->
                MoveValidator.validateMove(testBoard, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateNormalMoveShouldAllowPromotionMoveForBlack() {
        Board testBoard = createPromotionBoardBlack();
        Move move = new Move(null, 5, 1);

        assertDoesNotThrow(() ->
                MoveValidator.validateMove(testBoard, PlayerRole.BLACK, move)
        );
    }

    private Board createTestBoard() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(9, new Piece(PlayerRole.WHITE, false));
        testBoard.grid().put(13, new Piece(PlayerRole.WHITE, false));

        return testBoard;
    }

    private Board createCaptureBoard() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(9, new Piece(PlayerRole.WHITE, false));
        testBoard.grid().put(14, new Piece(PlayerRole.BLACK, false));

        return testBoard;
    }

    private Board createOwnPieceCaptureBoard() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(9, new Piece(PlayerRole.WHITE, false));
        testBoard.grid().put(13, new Piece(PlayerRole.WHITE, false));

        return testBoard;
    }

    private Board createPromotionBoardWhite() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(25, new Piece(PlayerRole.WHITE, false));

        return testBoard;
    }

    private Board createPromotionBoardBlack() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(5, new Piece(PlayerRole.BLACK, false));

        return testBoard;
    }

    private void clearBoard(Board board) {
        int totalCells = (board.size() * board.size()) / 2;
        for (int i = 1; i <= totalCells; i++) {
            board.grid().put(i, null);
        }
    }
}