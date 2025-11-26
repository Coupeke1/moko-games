package be.kdg.team22.tictactoeservice.domain.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GameDrawEvent(
        UUID gameId,
        List<UUID> players,
        Instant occurredAt
) {}