package be.kdg.team22.gameaclservice.events.outbound;

import be.kdg.team22.gameaclservice.events.inbound.ChessGameEndedEvent;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

public record GameEndedEvent(
        UUID instanceId,
        Instant occurredAt
) {
    public static GameEndedEvent convert(ChessGameEndedEvent event) {
        return new GameEndedEvent(
                event.gameId(),
                event.timestamp().toInstant(ZoneOffset.UTC)
        );
    }
}
