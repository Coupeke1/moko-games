package be.kdg.team22.tictactoeservice.domain.game;

import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class Board {
    private final int size;
    private final PlayerRole[][] grid;

    private Board(final int size) {
        this.size = size;
        this.grid = new PlayerRole[size][size];
    }

    private Board(final int size, final PlayerRole[][] grid) {
        this.size = size;
        this.grid = grid;
    }

    public static Board create(final int size) {
        return new Board(size);
    }

    public Board setCell(final int row, final int col, final PlayerRole cell) {
        PlayerRole[][] newGrid = new PlayerRole[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(this.grid[i], 0, newGrid[i], 0, size);
        }

        newGrid[row][col] = cell;
        return new Board(size, newGrid);
    }

    public int size() {
        return size;
    }

    public PlayerRole cell(final int row, final int col) {
        return grid[row][col];
    }

    public PlayerRole[][] grid() {
        return grid;
    }

    public GameStatus checkWinner(final int lastRow, final int lastCol, final PlayerRole role) {
        final int WIN_LENGTH = 3;

        if (checkDirection(lastRow, lastCol, role, 0, 1, WIN_LENGTH) ||      // horizontaal
                checkDirection(lastRow, lastCol, role, 1, 0, WIN_LENGTH) ||  // verticaal
                checkDirection(lastRow, lastCol, role, 1, 1, WIN_LENGTH) ||  // diagonaal \
                checkDirection(lastRow, lastCol, role, 1, -1, WIN_LENGTH)) { // diagonaal /

            return GameStatus.WON;
        }

        if (isBoardFull()) {
            return GameStatus.TIE;
        }

        return GameStatus.IN_PROGRESS;
    }

    private boolean checkDirection(final int startRow, final int startCol, final PlayerRole role,
                                   final int rowDelta, final int colDelta, final int winLength) {
        int count = 1;

        for (int i = 1; i < winLength; i++) {
            int newRow = startRow + i * rowDelta;
            int newCol = startCol + i * colDelta;

            if (newRow < 0 || newRow >= size || newCol < 0 || newCol >= size) {
                break;
            }

            if (this.cell(newRow, newCol) == role) {
                count++;
            } else {
                break;
            }
        }

        for (int i = 1; i < winLength; i++) {
            int newRow = startRow - i * rowDelta;
            int newCol = startCol - i * colDelta;

            if (newRow < 0 || newRow >= size || newCol < 0 || newCol >= size) {
                break;
            }

            if (this.cell(newRow, newCol) == role) {
                count++;
            } else {
                break;
            }
        }

        return count >= winLength;
    }

    private boolean isBoardFull() {
        int size = this.size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (this.cell(i, j) == null) {
                    return false;
                }
            }
        }
        return true;
    }
}