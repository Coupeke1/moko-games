package be.kdg.team22.tictactoeservice.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class Board {
    private final int size;
    private final Player[][] grid;

    private Board(int size) {
        this.size = size;
        this.grid = new Player[size][size];
    }

    private Board(int size, Player[][] grid) {
        this.size = size;
        this.grid = grid;
    }

    public static Board create(int size) {
        return new Board(size);
    }

    public int getSize() {
        return size;
    }

    public Player getCell(int row, int col) {
        return grid[row][col];
    }

    public Board setCell(int row, int col, Player cell) {
        Player[][] newGrid = new Player[size][size];
        for (int i = 0; i < newGrid.length; i++) {
            for (int j = 0; j < newGrid[i].length; j++) {
                newGrid[i][j] = cell;
            }
        }
        newGrid[row][col] = cell;
        return  new Board(size, newGrid);
    }

    public Player[][] getGrid() {
        return grid;
    }
}
