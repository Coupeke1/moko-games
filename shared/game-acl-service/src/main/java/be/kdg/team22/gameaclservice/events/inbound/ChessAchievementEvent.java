package be.kdg.team22.gameaclservice.events.inbound;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChessAchievementEvent(
        UUID gameId,
        UUID playerId,
        String playerName,
        String achievementType,
        String achievementDescription,
        ChessMessageType messageType,
        LocalDateTime timestamp
) {
}
