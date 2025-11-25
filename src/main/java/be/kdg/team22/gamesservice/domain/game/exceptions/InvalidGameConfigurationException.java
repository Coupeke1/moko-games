package be.kdg.team22.gamesservice.domain.game.exceptions;

public class InvalidGameConfigurationException extends RuntimeException {
    public InvalidGameConfigurationException(String message) {
        super(message);
    }
}
