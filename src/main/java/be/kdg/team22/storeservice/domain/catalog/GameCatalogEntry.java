package be.kdg.team22.storeservice.domain.catalog;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.math.BigDecimal;
import java.util.UUID;

@AggregateRoot
public class GameCatalogEntry {

    private final UUID id;
    private BigDecimal price;
    private GameCategory category;
    private double popularityScore;

    public GameCatalogEntry(UUID id,
                            BigDecimal price,
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

    public BigDecimal getPrice() {
        return price;
    }

    public GameCategory getCategory() {
        return category;
    }

    public double getPopularityScore() {
        return popularityScore;
    }

    public void update(BigDecimal price, GameCategory category) {
        this.price = price;
        this.category = category;
    }
}