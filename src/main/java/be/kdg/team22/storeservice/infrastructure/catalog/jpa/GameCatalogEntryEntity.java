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
    private String title;

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
                                  String title,
                                  double price,
                                  GameCategory category,
                                  double popularityScore) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.category = category;
        this.popularityScore = popularityScore;
    }

    public static GameCatalogEntryEntity fromDomain(GameCatalogEntry entry) {
        return new GameCatalogEntryEntity(
                entry.getId(),
                entry.getTitle(),
                entry.getPrice(),
                entry.getCategory(),
                entry.getPopularityScore()
        );
    }

    public GameCatalogEntry toDomain() {
        return new GameCatalogEntry(
                id,
                title,
                price,
                category,
                popularityScore
        );
    }
}