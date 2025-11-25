package be.kdg.team22.gamesservice.domain.game.exceptions;

public class GameBaseUrlInvalidException extends RuntimeException {
    public GameBaseUrlInvalidException() {
        super("BaseUrl cannot be empty");
    }
}