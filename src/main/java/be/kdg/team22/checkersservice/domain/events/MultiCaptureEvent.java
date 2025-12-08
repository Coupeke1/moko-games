package be.kdg.team22.checkersservice.domain.events;

import java.time.Instant;
import java.util.UUID;

public record MultiCaptureEvent(
        UUID gameId,
        UUID playerId,
        Instant occurredAt
) {}