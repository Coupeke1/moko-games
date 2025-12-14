package be.kdg.team22.gamesservice.domain.game.exceptions;

public class achievementKeyAlreadyExistisException extends RuntimeException {
    public achievementKeyAlreadyExistisException(String key) {
        super(String.format("An achievement key with name %s already exists", key));
    }
}