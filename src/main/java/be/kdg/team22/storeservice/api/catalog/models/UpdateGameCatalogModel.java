package be.kdg.team22.storeservice.api.catalog.models;

import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateGameCatalogModel(
        @NotNull @Positive BigDecimal price,
        @NotNull GameCategory category
) {
}
