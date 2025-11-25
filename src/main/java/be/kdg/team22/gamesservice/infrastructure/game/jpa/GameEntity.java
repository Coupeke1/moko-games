package be.kdg.team22.gamesservice.infrastructure.game.jpa;

import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "games")
public class GameEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "base_url")
    private String baseUrl;

    @Column(nullable = false, name = "start_endpoint")
    private String startEndpoint;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, name = "image_url")
    private String imageUrl;

    @Column(name = "store_url")
    private String storeUrl;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;

    protected GameEntity() {
    }

    public GameEntity(
            UUID id,
            String name,
            String baseUrl,
            String startEndpoint,
            String title,
            String description,
            BigDecimal price,
            String imageUrl,
            String storeUrl,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.startEndpoint = startEndpoint;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.storeUrl = storeUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static GameEntity fromDomain(Game game) {
        return new GameEntity(
                game.id().value(),
                game.name(),
                game.baseUrl(),
                game.startEndpoint(),
                game.title(),
                game.description(),
                game.price(),
                game.imageUrl(),
                game.storeUrl(),
                game.createdAt(),
                game.updatedAt()
        );
    }

    public Game toDomain() {
        return new Game(
                GameId.from(id),
                name,
                baseUrl,
                startEndpoint,
                title,
                description,
                price,
                imageUrl,
                storeUrl,
                createdAt,
                updatedAt
        );
    }

    public UUID id() {
        return id;
    }
}