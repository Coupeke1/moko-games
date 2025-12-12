package be.kdg.team22.userservice.infrastructure.games;

import java.math.BigDecimal;
import java.util.UUID;

public record GameDetailsResponse(
        UUID id,
        String name,
        String title,
        String description,
        BigDecimal price,
        String image,
        Boolean healthy
) {
}