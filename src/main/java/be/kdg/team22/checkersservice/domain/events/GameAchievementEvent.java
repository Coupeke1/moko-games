package be.kdg.team22.checkersservice.domain.events;

import java.time.Instant;
import java.util.UUID;

public record GameAchievementEvent(
        AchievementCode achievementCode,
        UUID gameId,
        UUID playerId,
        Instant occurredAt
) {
}
