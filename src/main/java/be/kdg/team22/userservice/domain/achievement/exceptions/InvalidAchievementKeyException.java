package be.kdg.team22.userservice.domain.achievement.exceptions;

public class InvalidAchievementKeyException extends RuntimeException {
    public InvalidAchievementKeyException() {
        super("Achievement key cannot be empty or null");
    }

    public InvalidAchievementKeyException(String key) {
        super(String.format("Achievement key '%s' can only contain capital alphanumeric characters and underscores. (must start with alphanumeric)", key));
    }
}