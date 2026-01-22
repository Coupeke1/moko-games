package be.kdg.team22.userservice.api.achievement.models;

import be.kdg.team22.userservice.domain.achievement.Achievement;
import be.kdg.team22.userservice.infrastructure.games.AchievementDetailsResponse;
import be.kdg.team22.userservice.infrastructure.games.GameDetailsResponse;

import java.time.Instant;
import java.util.UUID;

public record AchievementModel(
        UUID gameId,
        String code,
        String name,
        String description,
        int level,
        Instant unlockedAt,
        String gameName,
        String gameImage
) {
    public static AchievementModel from(Achievement achievement, AchievementDetailsResponse achievementDetails, GameDetailsResponse game) {
        return new AchievementModel(
                achievement.gameId().value(),
                achievement.key().key(),
                achievementDetails.name(),
                achievementDetails.description(),
                achievementDetails.levels(),
                achievement.unlockedAt(),
                game != null ? game.name() : null,
                game != null ? game.image() : null
        );
    }
}