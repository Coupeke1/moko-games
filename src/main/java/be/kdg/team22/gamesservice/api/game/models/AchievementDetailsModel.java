package be.kdg.team22.gamesservice.api.game.models;

import be.kdg.team22.gamesservice.domain.game.Achievement;

public record AchievementDetailsModel(
        String key,
        String name,
        String description,
        int levels
        ) {
    public static AchievementDetailsModel from(Achievement achievement) {
        return new AchievementDetailsModel(
                achievement.key().key(),
                achievement.name(),
                achievement.description(),
                achievement.levels()
        );
    }
}
