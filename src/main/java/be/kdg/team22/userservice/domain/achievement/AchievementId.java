package be.kdg.team22.userservice.domain.achievement;

import java.util.UUID;

public record AchievementId(UUID value) {
    public AchievementId {
        if (value == null) {
            throw new IllegalArgumentException("AchievementId cannot be null");
        }
    }

    public static AchievementId create() {
        return new AchievementId(UUID.randomUUID());
    }
}