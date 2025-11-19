package be.kdg.team22.tictactoeservice.domain;

import org.jmolecules.ddd.annotation.AggregateRoot;

@AggregateRoot
public class Game {
    private final GameId id;
    private Board board;
    private GameStatus status;
    private Player currentPlayer;

    public Game(int requestedSize) {
        this.id = GameId.create();
        this.board = Board.create(requestedSize);
        this.status = GameStatus.IN_PROGRESS;

        this.currentPlayer = Player.X;
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

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void reset() {
        if (status == GameStatus.IN_PROGRESS)
            throw new IllegalStateException("Cannot reset GameStatus when status is IN_PROGRESS");

        this.status = GameStatus.IN_PROGRESS;
        this.board = Board.create(this.board.getSize());

        this.currentPlayer = Player.X;
    }
}
