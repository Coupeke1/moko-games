package be.kdg.team22.tictactoeservice.domain;

import org.jmolecules.ddd.annotation.AggregateRoot;

@AggregateRoot
public class Game {
    private final GameId id;
    private Board board;
    private GameStatus status;
    private PlayerId playerXId;
    private PlayerId playerOId;
    private Player currentPlayer;

    public Game(int requestedSize, PlayerId playerXId, PlayerId playerOId) {
        this.id = GameId.create();
        this.board = Board.create(requestedSize);
        this.status = GameStatus.IN_PROGRESS;
        this.playerXId = playerXId;
        this.playerOId = playerOId;
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

    public PlayerId getPlayerXId() {
        return playerXId;
    }

    public PlayerId getPlayerOId() {
        return playerOId;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void reset() {
        if (status == GameStatus.IN_PROGRESS)
            throw new IllegalStateException("Cannot reset GameStatus when status is IN_PROGRESS");

        this.status = GameStatus.IN_PROGRESS;
        this.board = Board.create(this.board.getSize());

        PlayerId temp = this.playerXId;
        this.playerXId = this.playerOId;
        this.playerOId = temp;

        this.currentPlayer = Player.X;
    }
}
