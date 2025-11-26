package be.kdg.team22.userservice.domain.achievement;

import be.kdg.team22.userservice.domain.achievement.exceptions.AchievementException;

public record AchievementCode(String value) {
    public AchievementCode {
        if (value == null || value.isBlank()) {
            throw AchievementException.missingAchievementCode();
        }
    }
}