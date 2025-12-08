package be.kdg.team22.sessionservice.infrastructure.games;

import java.time.Instant;
import java.util.UUID;

public record GameResponse(
        UUID id,
        String name,
        String title,
        String description,
        String image,
        String frontendUrl,
        String startEndpoint,
        Instant createdAt,
        Instant updatedAt
) {
}