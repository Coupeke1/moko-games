package be.kdg.team22.storeservice.infrastructure.util;

import be.kdg.team22.storeservice.domain.catalog.Entry;

import java.util.Optional;

public class SortingUtils {
    public static int sort(Entry a, Entry b, Optional<String> sort) {
        return sort.map(s -> switch (s) {
            case "price" -> a.price().compareTo(b.price());
            case "popularity" -> Double.compare(a.popularity(), b.popularity());
            case "category" -> a.category().compareTo(b.category());
            default -> 0;
        }).orElse(0);
    }
}