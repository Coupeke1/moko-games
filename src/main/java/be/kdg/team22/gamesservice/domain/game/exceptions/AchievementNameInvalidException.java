package be.kdg.team22.gamesservice.domain.game.exceptions;

public class AchievementNameInvalidException extends RuntimeException {
    public AchievementNameInvalidException() {
        super("Achievement name cannot be empty");
    }
}