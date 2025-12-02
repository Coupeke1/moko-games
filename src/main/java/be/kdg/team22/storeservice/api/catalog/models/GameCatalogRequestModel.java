package be.kdg.team22.storeservice.api.catalog.models;

import be.kdg.team22.storeservice.domain.catalog.GameCategory;

import java.util.UUID;

public record GameCatalogRequestModel(
        UUID id,
        double price,
        GameCategory category,
        Double popularity
) {}
