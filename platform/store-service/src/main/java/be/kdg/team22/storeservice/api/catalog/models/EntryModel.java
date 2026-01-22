package be.kdg.team22.storeservice.api.catalog.models;

import be.kdg.team22.storeservice.domain.catalog.Entry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record EntryModel(
        UUID id,
        String title,
        String description,
        String image,
        BigDecimal price,
        GameCategory category,
        double popularity,
        int purchaseCount
) {
    public static EntryModel from(Entry entry, GameMetadataResponse meta) {
        return new EntryModel(
                entry.id().value(),
                meta.title(),
                meta.description(),
                meta.image(),
                entry.price(),
                entry.category(),
                entry.popularity(),
                entry.purchaseCount()
        );
    }
}