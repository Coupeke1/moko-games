package be.kdg.team22.gameaclservice.events.outbound;

import be.kdg.team22.gameaclservice.events.inbound.ChessAchievementEvent;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

public record GameAchievementEvent(
        String achievementCode,
        String gameName,
        UUID gameId,
        UUID playerId,
        Instant occurredAt
) {
    public static GameAchievementEvent convert(ChessAchievementEvent event, String gameName) {
        return new GameAchievementEvent(
                event.achievementType(),
                gameName,
                event.gameId(),
                event.playerId(),
                event.timestamp().toInstant(ZoneOffset.UTC)
        );
    }
}