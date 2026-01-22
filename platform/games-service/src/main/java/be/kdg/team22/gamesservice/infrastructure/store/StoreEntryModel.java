package be.kdg.team22.gamesservice.infrastructure.store;

import be.kdg.team22.gamesservice.domain.game.GameCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record StoreEntryModel(
        UUID id,
        BigDecimal price,
        GameCategory category,
        double popularity,
        int purchaseCount
) {
}