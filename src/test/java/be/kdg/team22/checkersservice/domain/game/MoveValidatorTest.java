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
    void validateNormalMoveShouldAllowValidForwardMoveForWhite() {
        Move move = new Move(null, 9, 13);

        assertDoesNotThrow(() ->
                MoveValidator.validateNormalMove(board, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateNormalMoveShouldAllowValidForwardMoveForBlack() {
        Move move = new Move(null, 24, 20);

        assertDoesNotThrow(() ->
                MoveValidator.validateNormalMove(board, PlayerRole.BLACK, move)
        );
    }

    @Test
    void validateNormalMoveShouldThrowWhenMovingEmptyCell() {
        Move move = new Move(null, 16, 20);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateNormalMove(board, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateNormalMoveShouldThrowWhenMovingOpponentPiece() {
        Move move = new Move(null, 9, 13);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateNormalMove(board, PlayerRole.BLACK, move)
        );
    }

    @Test
    void validateNormalMoveShouldThrowWhenMovingBackwardsAsWhite() {
        Move move = new Move(null, 13, 9);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateNormalMove(board, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateNormalMoveShouldThrowWhenMovingBackwardsAsBlack() {
        Move move = new Move(null, 20, 24);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateNormalMove(board, PlayerRole.BLACK, move)
        );
    }

    @Test
    void validateNormalMoveShouldThrowWhenTargetCellOccupied() {
        Board testBoard = createTestBoard();
        Move move = new Move(null, 9, 13);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateNormalMove(testBoard, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateNormalMoveShouldThrowWhenNotDiagonal() {
        Board testBoard = createTestBoard();
        Move move = new Move(null, 9, 10);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateNormalMove(testBoard, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateNormalMoveShouldThrowWhenMovingMoreThanOneStepWithoutCapture() {
        Move move = new Move(null, 9, 17);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateNormalMove(board, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateNormalMoveShouldAllowValidCapture() {
        Board testBoard = createCaptureBoard();
        Move move = new Move(null, 9, 18);

        assertDoesNotThrow(() ->
                MoveValidator.validateNormalMove(testBoard, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateNormalMoveShouldThrowWhenNoPieceToCapture() {
        Board testBoard = createTestBoard();
        Move move = new Move(null, 9, 18);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateNormalMove(testBoard, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateNormalMoveShouldThrowWhenCapturingOwnPiece() {
        Board testBoard = createOwnPieceCaptureBoard();
        Move move = new Move(null, 9, 18);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateNormalMove(testBoard, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateNormalMoveShouldThrowWhenCellOutsideBoard() {
        Move move = new Move(null, 1, 33);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateNormalMove(board, PlayerRole.WHITE, move)
        );
    }

    @Test
    void validateNormalMoveShouldThrowWhenMovingKing() {
        Board testBoard = createKingBoard();
        Move move = new Move(null, 9, 13);

        assertThrows(InvalidMoveException.class, () ->
                MoveValidator.validateNormalMove(testBoard, PlayerRole.WHITE, move)
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

    private Board createKingBoard() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(9, new Piece(PlayerRole.WHITE, true));

        return testBoard;
    }

    private void clearBoard(Board board) {
        int totalCells = (board.size() * board.size()) / 2;
        for (int i = 1; i <= totalCells; i++) {
            board.grid().put(i, null);
        }
    }
}