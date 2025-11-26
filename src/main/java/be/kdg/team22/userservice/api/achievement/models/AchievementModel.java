package be.kdg.team22.userservice.api.achievement.models;

import be.kdg.team22.userservice.domain.achievement.Achievement;

import java.time.Instant;
import java.util.UUID;

public record AchievementModel(
        UUID id,
        UUID gameId,
        String code,
        Instant unlockedAt
) {
    public static AchievementModel from(Achievement a) {
        return new AchievementModel(
                a.id().value(),
                a.gameId(),
                a.code().value(),
                a.unlockedAt()
        );
    }
}