package be.kdg.team22.userservice.events;

import java.util.UUID;

public record AchievementUnlockedEvent(
        UUID playerId,
        UUID gameId,
        String achievementKey,
        String achievementName,
        String description
) {
}

