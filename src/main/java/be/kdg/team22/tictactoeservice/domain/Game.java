package be.kdg.team22.tictactoeservice.domain;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.UUID;

@AggregateRoot
public class Game {
    private final GameId id;
    private final Board board;
    private GameStatus status;

    public Game(int requestedSize) {
        this.id = new GameId(UUID.randomUUID());
        this.board = Board.create(requestedSize);
        this.status = GameStatus.IN_PROGRESS;
    }

    public GameId getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public GameStatus getStatus() {
        return status;
    }
}
