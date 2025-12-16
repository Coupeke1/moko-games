package be.kdg.team22.gamesservice.domain.game.exceptions;

public class achievementKeyAlreadyExistsException extends RuntimeException {
    public achievementKeyAlreadyExistsException(String key) {
        super(String.format("An achievement key with name %s already exists", key));
    }
}