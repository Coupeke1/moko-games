package be.kdg.team22.userservice.api.achievement.models;

import be.kdg.team22.userservice.domain.achievement.Achievement;
import be.kdg.team22.userservice.domain.achievement.AchievementMetadata;
import be.kdg.team22.userservice.infrastructure.games.GameDetailsResponse;

import java.time.Instant;
import java.util.UUID;

public record AchievementModel(
        UUID id,
        UUID gameId,
        String code,
        String name,
        String description,
        int level,
        Instant unlockedAt,
        String gameImage
) {
    public static AchievementModel from(Achievement a, GameDetailsResponse game) {
        return new AchievementModel(
                a.id().value(),
                a.gameId(),
                a.code().value(),
                AchievementMetadata.getName(a.code().value()),
                AchievementMetadata.getDescription(a.code().value()),
                AchievementMetadata.getLevels(a.code().value()),
                a.unlockedAt(),
                game != null ? game.image() : null
        );
    }
}