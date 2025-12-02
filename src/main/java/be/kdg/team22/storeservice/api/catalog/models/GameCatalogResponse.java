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
        BigDecimal price,
        GameCategory category,
        double popularity,
        int purchaseCount
) {
    public static GameCatalogResponse from(GameCatalogEntry entry, GameMetadataResponse meta) {
        return new GameCatalogResponse(
                entry.getId(),
                meta.title(),
                meta.description(),
                meta.image(),
                entry.getPrice(),
                entry.getCategory(),
                entry.getPopularityScore(),
                entry.getPurchaseCount()
        );
    }
}