package be.kdg.team22.storeservice.domain.catalog;

import be.kdg.team22.storeservice.domain.catalog.exceptions.GameCatalogException;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

@AggregateRoot
public class Entry {
    private final GameId id;
    private BigDecimal price;
    private GameCategory category;
    private int purchaseCount;
    private double popularityScore;
    private final Collection<Post> posts;

    public Entry(final GameId id, final BigDecimal price, final GameCategory category, Collection<Post> posts) {
        this.id = id;
        this.price = price;
        this.category = category;
        this.posts = posts;
        this.purchaseCount = 0;
        this.popularityScore = calculatePopularityScore();
    }

    public Entry(final GameId id, final BigDecimal price, final GameCategory category, final int purchaseCount, Collection<Post> posts) {
        this.id = id;
        this.price = price;
        this.category = category;
        this.purchaseCount = purchaseCount;
        this.posts = posts;
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

    public GameId id() {
        return id;
    }

    public BigDecimal price() {
        return price;
    }

    public GameCategory category() {
        return category;
    }

    public double popularity() {
        return popularityScore;
    }

    public int purchaseCount() {
        return purchaseCount;
    }

    public Collection<Post> posts() {
        return posts;
    }

    public Optional<Post> findPost(final PostId id) {
        return posts.stream().filter(post -> post.id().equals(id)).findFirst();
    }
}