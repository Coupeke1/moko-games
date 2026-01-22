package be.kdg.team22.storeservice.infrastructure.games;

import java.time.Instant;
import java.util.UUID;

public record GameMetadataResponse(UUID id,
                                   String name,
                                   String title,
                                   String description,
                                   String image,
                                   Instant createdAt,
                                   Instant updatedAt) {}