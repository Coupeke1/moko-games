package be.kdg.team22.gamesservice.infrastructure.game.jpa;

import be.kdg.team22.gamesservice.domain.game.Achievement;
import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "games")
public class GameEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<AchievementEntity> achievements;

    @Column(nullable = false, name = "base_url")
    private String baseUrl;

    @Column(nullable = false, name = "frontend_url")
    private String frontendUrl;

    @Column(nullable = false, name = "start_endpoint")
    private String startEndpoint;
    @Column(nullable = false, name = "health_endpoint")
    private String healthEndpoint;

    @Column(name = "last_health_check")
    private Instant lastHealthCheck;

    @Column(nullable = false, name = "healthy")
    private boolean healthy;

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
            String healthEndpoint,
            Instant lastHealthCheck,
            boolean healthy,
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
        this.healthEndpoint = healthEndpoint;
        this.lastHealthCheck = lastHealthCheck;
        this.healthy = healthy;
        this.title = title;
        this.description = description;
        this.image = image;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static GameEntity fromDomain(Game game) {
        GameEntity gameEntity = new GameEntity(
                game.id().value(),
                game.name(),
                game.baseUrl(),
                game.frontendUrl(),
                game.startEndpoint(),
                game.healthEndpoint(),
                game.lastHealthCheck(),
                game.healthy(),
                game.title(),
                game.description(),
                game.image(),
                game.createdAt(),
                game.updatedAt()
        );

        gameEntity.achievements = game.achievements().stream()
                .map(achievement -> AchievementEntity.fromDomain(achievement, gameEntity))
                .toList();

        return gameEntity;
    }

    public Game toDomain() {
        Game game = new Game(
                GameId.from(id),
                name,
                baseUrl,
                frontendUrl,
                startEndpoint,
                healthEndpoint,
                lastHealthCheck,
                healthy,
                title,
                description,
                image,
                createdAt,
                updatedAt
        );

        List<Achievement> achievements = this.achievements.stream()
                .map(AchievementEntity::toDomain)
                .toList();
        game.addAchievements(achievements);

        return game;
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

    public String healthEndpoint() {
        return healthEndpoint;
    }

    public Instant lastHealthCheck() {
        return lastHealthCheck;
    }

    public boolean healthy() {
        return healthy;
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