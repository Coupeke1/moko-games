package be.kdg.team22.userservice.api.library.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LibraryGameModel(
        UUID id,
        String title,
        String description,
        BigDecimal price,
        String image,
        String url,
        Instant purchasedAt,
        boolean favourite
) {
}