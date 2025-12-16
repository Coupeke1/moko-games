package be.kdg.team22.gamesservice.domain.game.exceptions;

public class AchievementDescriptionInvalidException extends RuntimeException {
    public AchievementDescriptionInvalidException() {
        super("Achievement description cannot be empty");
    }
}