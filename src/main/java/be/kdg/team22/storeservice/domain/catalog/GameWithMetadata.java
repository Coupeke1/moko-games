package be.kdg.team22.storeservice.domain.catalog;

import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;

public record GameWithMetadata(
        GameCatalogEntry entry,
        GameMetadataResponse metadata
) {
}
