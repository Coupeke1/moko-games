package be.kdg.team22.checkersservice.domain.events;

import java.time.Instant;
import java.util.UUID;

public record GameAchievementEvent(
        String achievementCode,
        UUID gameId,
        UUID playerId,
        Instant occurredAt
) {
}
