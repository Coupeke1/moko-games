package be.kdg.team22.checkersservice.infrastructure.register;

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
        boolean healthy,
        Instant lastHealthCheck,
        Instant createdAt,
        Instant updatedAt
) {
}
