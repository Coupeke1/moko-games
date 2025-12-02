package be.kdg.team22.gamesservice.domain.game.exceptions;

public class GameFrontendUrlInvalidException extends RuntimeException {
    public GameFrontendUrlInvalidException() {
        super("frontend url cannot be empty");
    }
}