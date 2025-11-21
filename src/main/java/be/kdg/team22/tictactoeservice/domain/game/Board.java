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
}