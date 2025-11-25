package be.kdg.team22.gamesservice.infrastructure.game.jpa;

import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    protected GameEntity() {
    }

    public GameEntity(UUID id, String name, String baseUrl, String startEndpoint) {
        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.startEndpoint = startEndpoint;
    }

    public static GameEntity fromDomain(Game game) {
        return new GameEntity(
                game.id().value(),
                game.name(),
                game.baseUrl(),
                game.startEndpoint()
        );
    }

    public Game toDomain() {
        return new Game(
                GameId.from(id),
                name,
                baseUrl,
                startEndpoint
        );
    }

    public UUID id() {
        return id;
    }
}