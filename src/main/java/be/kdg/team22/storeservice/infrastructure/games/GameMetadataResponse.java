package be.kdg.team22.storeservice.infrastructure.games;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GameMetadataResponse(
        UUID id,
        String name,
        String title,
        String description,
        BigDecimal price,
        String image,
        String frontendUrl,
        String startEndpoint,
        Instant createdAt,
        Instant updatedAt
) {
}