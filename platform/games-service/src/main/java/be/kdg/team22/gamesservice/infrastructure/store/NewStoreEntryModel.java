package be.kdg.team22.gamesservice.infrastructure.store;

import be.kdg.team22.gamesservice.domain.game.GameCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record NewStoreEntryModel(
        @NotNull UUID id,
        @NotNull @Positive BigDecimal price,
        @NotNull GameCategory category
) {
}
