package be.kdg.team22.tictactoeservice.domain;

import be.kdg.team22.tictactoeservice.config.BoardSizeProperties;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class Board {
    private Player[][] grid;

    private Board(Player[][] grid) {
        this.grid = grid;
    }

    public static Board empty(int size) {
        return new Board(new Player[size][size]);
    }
}
