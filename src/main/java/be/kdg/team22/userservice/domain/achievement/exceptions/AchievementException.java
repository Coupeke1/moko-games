package be.kdg.team22.userservice.domain.achievement.exceptions;

public class AchievementException extends RuntimeException {

    private AchievementException(String message) {
        super(message);
    }

    public static AchievementException missingProfileId() {
        return new AchievementException("profileId cannot be null");
    }

    public static AchievementException missingAchievementCode() {
        return new AchievementException("achievementCode cannot be null or blank");
    }

    public static AchievementException missingUnlockedAt() {
        return new AchievementException("unlockedAt cannot be null");
    }
}