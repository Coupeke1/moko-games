package be.kdg.team22.gamesservice.domain.game;

import be.kdg.team22.gamesservice.domain.game.exceptions.InvalidGameConfigurationException;
import org.jmolecules.ddd.annotation.AggregateRoot;

@AggregateRoot
public class Game {

    private final GameId id;
    private final String name;
    private final String baseUrl;
    private final String startEndpoint;

    public Game(GameId id, String name, String baseUrl, String startEndpoint) {
        if (id == null) throw new IllegalArgumentException("GameId cannot be null");
        if (name == null || name.isBlank())
            throw new InvalidGameConfigurationException("Game name cannot be empty");
        if (baseUrl == null || baseUrl.isBlank())
            throw new InvalidGameConfigurationException("BaseUrl cannot be empty");
        if (startEndpoint == null || startEndpoint.isBlank())
            throw new InvalidGameConfigurationException("Start endpoint cannot be empty");

        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.startEndpoint = startEndpoint;
    }

    public GameId id() {
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
}