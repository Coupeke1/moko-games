package be.kdg.team22.tictactoeservice.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class Board {
    private final int size;
    private Player[][] grid;

    private Board(int size) {
        this.size = size;
        this.grid = new Player[size][size];
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

    public void setCell(int row, int col, Player cell) {
        grid[row][col] = cell;
    }

    public Player[][] getGrid() {
        return grid;
    }
}
