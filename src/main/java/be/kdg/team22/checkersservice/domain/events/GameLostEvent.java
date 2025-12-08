package be.kdg.team22.checkersservice.domain.events;

import java.time.Instant;
import java.util.UUID;

public record GameLostEvent(
        UUID gameId,
        UUID loserId,
        Instant occurredAt
) {}