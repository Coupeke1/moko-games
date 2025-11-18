package be.kdg.team22.tictactoeservice.domain;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.UUID;

@AggregateRoot
public class Game {
    private final GameId id;
    private Board board;

    private Game(GameId id, Board board) {
        this.id = id;
        this.board = board;
    }

    public static Game create(int size) {
        return new Game(
                new GameId(UUID.randomUUID()),
                Board.empty(size)
        );
    }
}
