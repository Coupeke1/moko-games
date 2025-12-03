package be.kdg.team22.checkersservice.domain.board;

import be.kdg.team22.checkersservice.domain.game.exceptions.BoardSizeException;
import be.kdg.team22.checkersservice.domain.game.exceptions.OutsidePlayingFieldException;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ValueObject
public class Board {
    private final int size;
    private final Map<Integer, Piece> grid;

    private Board(final int size) {
        this.size = size;
        this.grid = new HashMap<>();
    }

    public static Board create(final int size) {
        if (size != 8 && size != 10) {
            throw new BoardSizeException();
        }

        Board board = new Board(size);
        board.setupInitialPieces();

        return board;
    }

    public void setupInitialPieces() {
        int totalCells = (size * size) / 2;
        for (int cell = 1; cell <= totalCells; cell++) {
            grid.put(cell, null);
        }

        int rowsPerPlayer = switch (size) {
            case 8 -> 3;
            case 10 -> 4;
            default -> throw new BoardSizeException();
        };

        placePlayerPieces(PlayerRole.WHITE, 1, rowsPerPlayer);
        placePlayerPieces(PlayerRole.BLACK, size + 1 - rowsPerPlayer, size);
    }

    private void placePlayerPieces(PlayerRole player, int startRow, int endRow) {
        for (int row = startRow; row <= endRow; row++) {
            for (int cell : getCellsInRow(row)) {
                grid.put(cell, new Piece(player, false));
            }
        }
    }

    private int[] getCellsInRow(int row) {
        final int cellsPerRow = size / 2;
        int[] cells = new int[cellsPerRow];

        int startCell = (row - 1) * cellsPerRow + 1;
        for (int i = 0; i < cellsPerRow; i++) {
            cells[i] = startCell + i;
        }

        return cells;
    }

    public String[][] state() {
        String[][] state = new String[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if ((row + col) % 2 != 0) {
                    int cellNumber = convertCoordinatesToCellNumber(row, col);
                    Piece piece = grid.get(cellNumber);
                    state[row][col] = piece != null ? piece.toString() : "  ";
                } else {
                    state[row][col] = "  ";
                }
            }
        }
        return state;
    }

    public int convertCoordinatesToCellNumber(int row, int col) {
        if ((row + col) % 2 == 0) {
            throw new OutsidePlayingFieldException();
        }

        return (row * (size / 2)) + (col / 2) + 1;
    }

    public int[] convertCellNumberToCoordinates(int cellNumber) {
        if (cellNumber < 1 || cellNumber > (size * size) / 2) {
            throw new OutsidePlayingFieldException();
        }

        int cellsPerRow = size / 2;
        int row = (cellNumber - 1) / cellsPerRow;
        int colInRow = (cellNumber - 1) % cellsPerRow;

        int col;
        if (row % 2 == 0) {
            col = 2 * colInRow + 1;
        } else {
            col = 2 * colInRow;
        }

        return new int[]{row, col};
    }

    public Optional<Piece> pieceAt(int cellNumber) {
        if (cellNumber < 1 || cellNumber > (size * size) / 2) {
            return Optional.empty();
        }
        return Optional.ofNullable(grid.get(cellNumber));
    }

    public int size() {
        return size;
    }
}