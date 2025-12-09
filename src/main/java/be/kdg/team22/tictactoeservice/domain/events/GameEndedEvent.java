package be.kdg.team22.tictactoeservice.domain.events;

import java.time.Instant;
import java.util.UUID;

public record GameEndedEvent(
        UUID instanceId,
        Instant occurredAt
) {
}