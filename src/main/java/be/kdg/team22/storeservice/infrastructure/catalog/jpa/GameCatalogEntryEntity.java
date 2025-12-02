package be.kdg.team22.storeservice.infrastructure.catalog.jpa;

import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "store_catalog")
public class GameCatalogEntryEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameCategory category;

    @Column(nullable = false, name = "popularity_score")
    private double popularityScore;

    protected GameCatalogEntryEntity() {
    }

    public GameCatalogEntryEntity(UUID id,
                                  double price,
                                  GameCategory category,
                                  double popularityScore) {
        this.id = id;
        this.price = price;
        this.category = category;
        this.popularityScore = popularityScore;
    }

    public static GameCatalogEntryEntity fromDomain(GameCatalogEntry entry) {
        return new GameCatalogEntryEntity(
                entry.getId(),
                entry.getPrice(),
                entry.getCategory(),
                entry.getPopularityScore()
        );
    }

    public GameCatalogEntry toDomain() {
        return new GameCatalogEntry(
                id,
                price,
                category,
                popularityScore
        );
    }

    public UUID getId() { return id; }
    public double getPrice() { return price; }
    public GameCategory getCategory() { return category; }
    public double getPopularityScore() { return popularityScore; }
}