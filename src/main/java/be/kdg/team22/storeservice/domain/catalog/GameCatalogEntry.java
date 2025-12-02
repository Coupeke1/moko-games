package be.kdg.team22.storeservice.domain.catalog;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.UUID;

@AggregateRoot
public class GameCatalogEntry {
    private final UUID id;
    private String title;
    private double price;
    private GameCategory category;
    private final double popularityScore;

    public GameCatalogEntry(UUID id, String title, double price,
                            GameCategory category, double popularityScore) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.category = category;
        this.popularityScore = popularityScore;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public GameCategory getCategory() {
        return category;
    }

    public double getPopularityScore() {
        return popularityScore;
    }

    public void update(String title, double price, GameCategory category) {
        this.title = title;
        this.price = price;
        this.category = category;
    }
}