package be.kdg.team22.storeservice.domain.catalog;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.UUID;

@AggregateRoot
public class GameCatalogEntry {

    private final UUID id;
    private double price;
    private GameCategory category;
    private double popularityScore;

    public GameCatalogEntry(UUID id,
                            double price,
                            GameCategory category,
                            double popularityScore) {
        this.id = id;
        this.price = price;
        this.category = category;
        this.popularityScore = popularityScore;
    }

    public UUID getId() {
        return id;
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

    public void update(double price, GameCategory category) {
        this.price = price;
        this.category = category;
    }
}