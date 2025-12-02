package be.kdg.team22.storeservice.api.catalog.models;

import be.kdg.team22.storeservice.domain.catalog.GameCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record GameCatalogRequestModel(
        UUID id,
        BigDecimal price,
        GameCategory category,
        Double popularity
) {}
