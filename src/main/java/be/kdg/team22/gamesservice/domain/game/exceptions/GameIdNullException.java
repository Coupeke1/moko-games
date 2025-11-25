package be.kdg.team22.gamesservice.domain.game.exceptions;

public class GameIdNullException extends RuntimeException {
    public GameIdNullException() {
        super("GameId cannot be null");
    }
}