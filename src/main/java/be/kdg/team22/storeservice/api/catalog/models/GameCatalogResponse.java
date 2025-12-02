package be.kdg.team22.storeservice.api.catalog.models;

import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;

import java.util.UUID;

public record GameCatalogResponse(
        UUID id,
        String title,
        double price,
        GameCategory category,
        double popularity
) {
    public static GameCatalogResponse from(GameCatalogEntry entry) {
        return new GameCatalogResponse(
                entry.getId(),
                entry.getTitle(),
                entry.getPrice(),
                entry.getCategory(),
                entry.getPopularityScore()
        );
    }
}
