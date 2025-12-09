package be.kdg.team22.gamesservice.domain.game.exceptions;

public class GameHealthEndpointInvalidException extends RuntimeException {
    public GameHealthEndpointInvalidException() {
        super("Game health point cannot be null or empty");
    }
}
