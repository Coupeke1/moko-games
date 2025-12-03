package be.kdg.team22.storeservice.domain.catalog;

import be.kdg.team22.storeservice.domain.catalog.exceptions.GameCatalogException;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.math.BigDecimal;
import java.util.UUID;

@AggregateRoot
public class GameCatalogEntry {
    private final UUID id;
    private BigDecimal price;
    private GameCategory category;
    private int purchaseCount;
    private double popularityScore;

    public GameCatalogEntry(final UUID id,
                            final BigDecimal price,
                            final GameCategory category) {
        this.id = id;
        this.price = price;
        this.category = category;
        this.purchaseCount = 0;
        this.popularityScore = calculatePopularityScore();
    }

    public GameCatalogEntry(final UUID id,
                            final BigDecimal price,
                            final GameCategory category,
                            final int purchaseCount) {
        this.id = id;
        this.price = price;
        this.category = category;
        this.purchaseCount = purchaseCount;
        this.popularityScore = calculatePopularityScore();
    }

    public void updatePriceAndCategory(final BigDecimal newPrice, final GameCategory newCategory) {
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw GameCatalogException.PriceMustBePositive(newPrice);
        }
        this.price = newPrice;
        this.category = newCategory;
    }

    public void recordPurchase() {
        this.purchaseCount++;
        this.popularityScore = calculatePopularityScore();
    }

    private double calculatePopularityScore() {
        return Math.log10(purchaseCount + 1) * 10;
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

    public int getPurchaseCount() {
        return purchaseCount;
    }

}