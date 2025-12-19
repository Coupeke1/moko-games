package be.kdg.team22.gameaclservice.events.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record ChessAchievementEvent(
        UUID gameId,
        UUID playerId,
        String playerName,
        String achievementType,
        String achievementDescription,
        String messageType,
        LocalDateTime timestamp
) {
}
