package be.kdg.team22.gamesservice.domain.game.exceptions;

public class AchievementKeyAlreadyExistsException extends RuntimeException {
    public AchievementKeyAlreadyExistsException(String key) {
        super(String.format("An achievement key with name %s already exists", key));
    }
}