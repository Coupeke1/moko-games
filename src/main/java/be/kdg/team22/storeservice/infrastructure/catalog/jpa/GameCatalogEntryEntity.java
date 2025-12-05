package be.kdg.team22.storeservice.infrastructure.catalog.jpa;

import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "store_catalog")
public class GameCatalogEntryEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameCategory category;

    @Column(nullable = false, name = "purchase_count")
    private int purchaseCount;

    protected GameCatalogEntryEntity() {
    }

    public GameCatalogEntryEntity(final UUID id,
                                  final BigDecimal price,
                                  final GameCategory category,
                                  final int purchaseCount) {
        this.id = id;
        this.price = price;
        this.category = category;
        this.purchaseCount = purchaseCount;
    }

    public static GameCatalogEntryEntity from(final GameCatalogEntry entry) {
        return new GameCatalogEntryEntity(
                entry.getId(),
                entry.getPrice(),
                entry.getCategory(),
                entry.getPurchaseCount()
        );
    }

    public GameCatalogEntry to() {
        return new GameCatalogEntry(
                this.id,
                this.price,
                this.category,
                this.purchaseCount
        );
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public GameCategory getCategory() {
        return category;
    }

    public void setCategory(final GameCategory category) {
        this.category = category;
    }

    public int getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(final int purchaseCount) {
        this.purchaseCount = purchaseCount;
    }
}