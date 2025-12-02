package be.kdg.team22.storeservice.api.catalog.models;

import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record GameCatalogResponse(
        UUID id,
        String title,
        String description,
        String image,
        String frontendUrl,
        String startEndpoint,
        BigDecimal price,
        GameCategory category,
        double popularity
) {
    public static GameCatalogResponse from(GameCatalogEntry entry, GameMetadataResponse meta) {
        return new GameCatalogResponse(
                entry.getId(),
                meta.title(),
                meta.description(),
                meta.image(),
                meta.frontendUrl(),
                meta.startEndpoint(),
                entry.getPrice(),
                entry.getCategory(),
                entry.getPopularityScore()
        );
    }
}