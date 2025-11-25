package be.kdg.team22.gamesservice.infrastructure.db.entities;

import be.kdg.team22.gamesservice.domain.Game;
import be.kdg.team22.gamesservice.domain.GameId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "game")
public class GameEntity {

    @Id
    private UUID id;

    private String name;
    private String baseUrl;
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

    public Game toDomain() {
        return new Game(
                new GameId(id),
                name,
                baseUrl,
                startEndpoint
        );
    }
}
