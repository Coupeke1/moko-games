package be.kdg.team22.checkersservice.domain.game;

import be.kdg.team22.checkersservice.domain.board.Board;
import be.kdg.team22.checkersservice.domain.board.Piece;
import be.kdg.team22.checkersservice.domain.game.exceptions.BoardSizeException;
import be.kdg.team22.checkersservice.domain.game.exceptions.OutsidePlayingFieldException;
import be.kdg.team22.checkersservice.domain.move.Move;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    private Board board8x8;
    private Board board10x10;

    @BeforeEach
    public void setup() {
        board8x8 = Board.create(8);
        board10x10 = Board.create(10);
    }

    @Test
    void shouldCreateValidBoardSizes() {
        assertNotNull(board8x8);
        assertNotNull(board10x10);
    }

    @Test
    void createShouldThrowWhenInvalidSize() {
        assertThrows(BoardSizeException.class, () -> Board.create(6));
        assertThrows(BoardSizeException.class, () -> Board.create(12));
        assertThrows(BoardSizeException.class, () -> Board.create(0));
        assertThrows(BoardSizeException.class, () -> Board.create(-1));
    }

    @Test
    void stateShouldReturnCorrectArraySize() {
        List<List<String>> state8x8 = board8x8.state();
        assertEquals(8, state8x8.size());
        assertEquals(8, state8x8.getFirst().size());

        List<List<String>> state10x10 = board10x10.state();
        assertEquals(10, state10x10.size());
        assertEquals(10, state10x10.getFirst().size());
    }

    @Test
    void stateShouldHaveEmptyWhiteSquares() {
        List<List<String>> state = board8x8.state();

        assertEquals("  ", state.get(0).get(0));
        assertEquals("  ", state.get(0).get(2));
        assertEquals("  ", state.get(1).get(1));
        assertEquals("  ", state.get(1).get(3));
    }

    @Test
    void stateShouldShowPiecesOnBlackSquares() {
        List<List<String>> state = board8x8.state();

        assertNotEquals("  ", state.get(0).get(1));
        assertNotEquals("  ", state.get(0).get(3));
        assertNotEquals("  ", state.get(1).get(0));
        assertNotEquals("  ", state.get(1).get(2));
    }

    @Test
    void setupInitialPiecesShouldPlaceCorrectNumberOfPieces() {
        List<List<String>> state8x8 = board8x8.state();
        int pieceCount8x8 = countPieces(state8x8);
        assertEquals(24, pieceCount8x8); // 3 rows per player * 4 pieces per row * 2 players

        List<List<String>> state10x10 = board10x10.state();
        int pieceCount10x10 = countPieces(state10x10);
        assertEquals(40, pieceCount10x10); // 4 rows per player * 5 pieces per row * 2 players
    }

    @Test
    void setupInitialPiecesShouldResetMovesSinceLastCapture() throws NoSuchFieldException, IllegalAccessException {
        Field movesField = Board.class.getDeclaredField("movesSinceLastCapture");
        movesField.setAccessible(true);
        movesField.set(board8x8, 30);

        assertEquals(30, board8x8.movesSinceLastCapture());
        board8x8.setupInitialPieces();
        assertEquals(0, board8x8.movesSinceLastCapture());
    }

    @Test
    void piecesShouldBeInCorrectPositionsFor8x8() {
        List<List<String>> state = board8x8.state();

        assertPieceColor(state.get(0).get(1), PlayerRole.WHITE);
        assertPieceColor(state.get(0).get(3), PlayerRole.WHITE);
        assertPieceColor(state.get(0).get(5), PlayerRole.WHITE);
        assertPieceColor(state.get(0).get(7), PlayerRole.WHITE);

        assertPieceColor(state.get(1).get(0), PlayerRole.WHITE);
        assertPieceColor(state.get(1).get(2), PlayerRole.WHITE);
        assertPieceColor(state.get(1).get(4), PlayerRole.WHITE);
        assertPieceColor(state.get(1).get(6), PlayerRole.WHITE);

        assertPieceColor(state.get(2).get(1), PlayerRole.WHITE);
        assertPieceColor(state.get(2).get(3), PlayerRole.WHITE);
        assertPieceColor(state.get(2).get(5), PlayerRole.WHITE);
        assertPieceColor(state.get(2).get(7), PlayerRole.WHITE);

        for (int row = 3; row < 5; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 != 0) {
                    assertEquals("  ", state.get(row).get(col));
                }
            }
        }

        assertPieceColor(state.get(5).get(0), PlayerRole.BLACK);
        assertPieceColor(state.get(5).get(2), PlayerRole.BLACK);
        assertPieceColor(state.get(5).get(4), PlayerRole.BLACK);
        assertPieceColor(state.get(5).get(6), PlayerRole.BLACK);

        assertPieceColor(state.get(6).get(1), PlayerRole.BLACK);
        assertPieceColor(state.get(6).get(3), PlayerRole.BLACK);
        assertPieceColor(state.get(6).get(5), PlayerRole.BLACK);
        assertPieceColor(state.get(6).get(7), PlayerRole.BLACK);

        assertPieceColor(state.get(7).get(0), PlayerRole.BLACK);
        assertPieceColor(state.get(7).get(2), PlayerRole.BLACK);
        assertPieceColor(state.get(7).get(4), PlayerRole.BLACK);
        assertPieceColor(state.get(7).get(6), PlayerRole.BLACK);
    }

    @Test
    void piecesShouldBeInCorrectPositionsFor10x10() {
        List<List<String>> state = board10x10.state();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row + col) % 2 != 0) {
                    assertPieceColor(state.get(row).get(col), PlayerRole.WHITE);
                }
            }
        }

        for (int row = 4; row < 6; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row + col) % 2 != 0) {
                    assertEquals("  ", state.get(row).get(col));
                }
            }
        }

        for (int row = 6; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row + col) % 2 != 0) {
                    assertPieceColor(state.get(row).get(col), PlayerRole.BLACK);
                }
            }
        }
    }

    @Test
    void convertCoordinatesToCellNumberShouldWorkCorrectly() throws Exception {
        Board board = Board.create(8);

        assertEquals(1, invokeConvertCoordinates(board, 0, 1));
        assertEquals(2, invokeConvertCoordinates(board, 0, 3));
        assertEquals(5, invokeConvertCoordinates(board, 1, 0));
        assertEquals(6, invokeConvertCoordinates(board, 1, 2));
        assertEquals(32, invokeConvertCoordinates(board, 7, 6));
    }

    @Test
    void convertCoordinatesToCellNumberShouldThrowOnWhiteSquares() {
        Board board = Board.create(8);

        assertThrows(OutsidePlayingFieldException.class, () -> {
            try {
                invokeConvertCoordinates(board, 0, 0);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });

        assertThrows(OutsidePlayingFieldException.class, () -> {
            try {
                invokeConvertCoordinates(board, 0, 2);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });

        assertThrows(OutsidePlayingFieldException.class, () -> {
            try {
                invokeConvertCoordinates(board, 1, 1);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });

        assertThrows(OutsidePlayingFieldException.class, () -> {
            try {
                invokeConvertCoordinates(board, 7, 7);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    void convertCoordinatesToCellNumberShouldHandle10x10Board() throws Exception {
        Board board = Board.create(10);

        assertEquals(1, invokeConvertCoordinates(board, 0, 1));
        assertEquals(5, invokeConvertCoordinates(board, 0, 9));
        assertEquals(6, invokeConvertCoordinates(board, 1, 0));
        assertEquals(10, invokeConvertCoordinates(board, 1, 8));
        assertEquals(50, invokeConvertCoordinates(board, 9, 8));
    }

    @Test
    void getCellsInRowShouldReturnCorrectCellsFor8x8() {
        int[] cellsRow1 = invokeGetCellsInRow(board8x8, 1);
        assertArrayEquals(new int[]{1, 2, 3, 4}, cellsRow1);

        int[] cellsRow2 = invokeGetCellsInRow(board8x8, 2);
        assertArrayEquals(new int[]{5, 6, 7, 8}, cellsRow2);

        int[] cellsRow8 = invokeGetCellsInRow(board8x8, 8);
        assertArrayEquals(new int[]{29, 30, 31, 32}, cellsRow8);
    }

    @Test
    void getCellsInRowShouldReturnCorrectCellsFor10x10() {
        int[] cellsRow1 = invokeGetCellsInRow(board10x10, 1);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, cellsRow1);

        int[] cellsRow10 = invokeGetCellsInRow(board10x10, 10);
        assertArrayEquals(new int[]{46, 47, 48, 49, 50}, cellsRow10);
    }

    @Test
    void initialPiecesShouldNotBeKings() {
        List<List<String>> state8x8 = board8x8.state();
        List<List<String>> state10x10 = board10x10.state();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 != 0 && !state8x8.get(row).get(col).equals("  ")) {
                    String piece = state8x8.get(row).get(col);
                    assertTrue(piece.endsWith(" "));
                }
            }
        }

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row + col) % 2 != 0 && !state10x10.get(row).get(col).equals("  ")) {
                    String piece = state10x10.get(row).get(col);
                    assertTrue(piece.endsWith(" "));
                }
            }
        }
    }

    @Test
    void moveShouldIncreaseMovesSinceLastCaptureWhenNotCapturing() {
        Board board = Board.create(8);
        int initialMovesSinceLastCapture = board.movesSinceLastCapture();
        board.move(new Move(PlayerId.create(), List.of(22, 17)));

        assertEquals(initialMovesSinceLastCapture + 1, board.movesSinceLastCapture());
    }

    @Test
    void moveShouldResetMovesSinceLastCaptureWhenCapturing() throws NoSuchFieldException, IllegalAccessException {
        Board board = Board.create(8);
        Field movesField = Board.class.getDeclaredField("movesSinceLastCapture");
        movesField.setAccessible(true);
        movesField.set(board, 39);
        board.grid().put(18, new Piece(PlayerRole.WHITE, false));

        board.move(new Move(PlayerId.create(), List.of(22, 15)));

        assertEquals(0, board.movesSinceLastCapture());
    }

    @Test
    void checkWinConditionsShouldReturnRunningWhenNoConditionsReached() {
        Board board = Board.create(8);

        assertEquals(GameStatus.RUNNING, board.checkWinConditions());
    }

    @Test
    void checkWinConditionsShouldReturnDrawWhenNoCapturesForFortyTurns() throws NoSuchFieldException, IllegalAccessException {
        Board board = Board.create(8);
        Field movesField = Board.class.getDeclaredField("movesSinceLastCapture");
        movesField.setAccessible(true);
        movesField.set(board, 40);

        assertEquals(GameStatus.DRAW, board.checkWinConditions());
    }

    @Test
    void checkWinConditionsShouldReturnDrawWhenNoCapturesForMoreThanFortyTurns() throws NoSuchFieldException, IllegalAccessException {
        Board board = Board.create(8);
        Field movesField = Board.class.getDeclaredField("movesSinceLastCapture");
        movesField.setAccessible(true);
        movesField.set(board, 41);

        assertEquals(GameStatus.DRAW, board.checkWinConditions());
    }

    @Test
    void checkWinConditionsShouldReturnDrawWhenNoPiecesLeft() {
        Board board = Board.create(8);
        clearColorFromBoard(board, PlayerRole.WHITE);
        clearColorFromBoard(board, PlayerRole.BLACK);

        assertEquals(GameStatus.DRAW, board.checkWinConditions());
    }

    @Test
    void checkWinConditionsShouldReturnBlackWinWhenNoWhitePiecesLeft() {
        Board board = Board.create(8);
        clearColorFromBoard(board, PlayerRole.WHITE);

        assertEquals(GameStatus.BLACK_WIN, board.checkWinConditions());
    }

    @Test
    void checkWinConditionsShouldReturnWhiteWinWhenNoBlackPiecesLeft() {
        Board board = Board.create(8);
        clearColorFromBoard(board, PlayerRole.BLACK);

        assertEquals(GameStatus.WHITE_WIN, board.checkWinConditions());
    }

    // Helper methods
    private int countPieces(List<List<String>> state) {
        int count = 0;
        for (List<String> row : state) {
            for (String cell : row) {
                if (cell != null && !cell.equals("  ")) {
                    count++;
                }
            }
        }
        return count;
    }

    private void assertPieceColor(String pieceString, PlayerRole expectedRole) {
        assertNotNull(pieceString);
        assertNotEquals("  ", pieceString);

        char expectedChar = expectedRole.symbol();
        assertEquals(expectedChar, pieceString.charAt(0));
    }

    private int invokeConvertCoordinates(Board board, int row, int col) throws Exception {
        var method = Board.class.getDeclaredMethod("convertCoordinatesToCellNumber", int.class, int.class);
        method.setAccessible(true);
        return (int) method.invoke(board, row, col);
    }

    private int[] invokeGetCellsInRow(Board board, int row) {
        try {
            var method = Board.class.getDeclaredMethod("getCellsInRow", int.class);
            method.setAccessible(true);
            return (int[]) method.invoke(board, row);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void clearColorFromBoard(Board board, PlayerRole color) {
        int totalCells = (board.size() * board.size()) / 2;
        for (int i = 1; i <= totalCells; i++) {
            if (board.grid().get(i) != null && board.grid().get(i).color().equals(color)) {
                board.grid().put(i, null);
            }
        }
    }
}