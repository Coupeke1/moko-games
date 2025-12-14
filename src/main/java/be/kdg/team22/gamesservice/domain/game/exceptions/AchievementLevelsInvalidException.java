package be.kdg.team22.gamesservice.domain.game.exceptions;

public class AchievementLevelsInvalidException extends RuntimeException {
    public AchievementLevelsInvalidException() {
        super("Achievement levels cannot be null or negative");
    }
}