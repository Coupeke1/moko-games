package be.kdg.team22.userservice.api.achievement.models;

import java.util.List;

public record AchievementListModel(
        List<AchievementModel> achievements
) {
}