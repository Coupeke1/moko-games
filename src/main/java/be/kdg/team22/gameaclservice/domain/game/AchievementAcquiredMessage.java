package be.kdg.team22.gameaclservice.domain.game;

import java.time.LocalDateTime;

public record AchievementAcquiredMessage(
        String gameId,
        String playerId,
        String playerName,
        String achievementType,
        String achievementDescription,
        String messageType,
        LocalDateTime timestamp
) {
}
