package be.kdg.team22.communicationservice.infrastructure.messaging.events.achievements;

import java.util.UUID;

public record AchievementUnlockedEvent(
        UUID playerId,
        UUID achievementId,
        String achievementName,
        String description
) {
}