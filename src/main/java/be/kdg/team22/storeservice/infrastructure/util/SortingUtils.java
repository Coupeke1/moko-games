package be.kdg.team22.storeservice.infrastructure.util;

import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;

import java.util.Optional;

public class SortingUtils {
    public static int sort(GameCatalogEntry a, GameCatalogEntry b, Optional<String> sort) {
        return sort.map(s -> switch (s) {
            case "price" -> a.getPrice().compareTo(b.getPrice());
            case "popularity" -> Double.compare(a.getPopularityScore(), b.getPopularityScore());
            case "category" -> a.getCategory().compareTo(b.getCategory());
            default -> 0;
        }).orElse(0);
    }
}