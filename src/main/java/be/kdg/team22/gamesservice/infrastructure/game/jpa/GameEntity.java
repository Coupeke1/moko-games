package be.kdg.team22.gamesservice.infrastructure.game.jpa;

import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column(nullable = false, name = "frontend_url")
    private String frontendUrl;

    @Column(nullable = false, name = "start_endpoint")
    private String startEndpoint;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, name = "image_url")
    private String image;

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
            String frontendUrl,
            String startEndpoint,
            String title,
            String description,
            String image,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.frontendUrl = frontendUrl;
        this.startEndpoint = startEndpoint;
        this.title = title;
        this.description = description;
        this.image = image;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static GameEntity fromDomain(Game game) {
        return new GameEntity(
                game.id().value(),
                game.name(),
                game.baseUrl(),
                game.frontendUrl(),
                game.startEndpoint(),
                game.title(),
                game.description(),
                game.image(),
                game.createdAt(),
                game.updatedAt()
        );
    }

    public Game toDomain() {
        return new Game(
                GameId.from(id),
                name,
                baseUrl,
                frontendUrl,
                startEndpoint,
                title,
                description,
                image,
                createdAt,
                updatedAt
        );
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String startEndpoint() {
        return startEndpoint;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String image() {
        return image;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}