package be.kdg.team22.userservice.domain.achievement;

public record AchievementCode(String value) {
    public AchievementCode {
        if (value == null || value.isBlank()) {
            //TODO custom exception
            throw new IllegalArgumentException("AchievementCode cannot be null or blank");
        }
    }
}