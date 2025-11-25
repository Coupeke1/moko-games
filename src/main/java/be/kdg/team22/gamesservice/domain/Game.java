package be.kdg.team22.gamesservice.domain;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
public class Game {

    @Identity
    private final GameId id;

    private final String name;
    private final String baseUrl;
    private final String startEndpoint;

    public Game(GameId id, String name, String baseUrl, String startEndpoint) {
        if (id == null) throw new IllegalArgumentException("Game id cannot be null");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Game name cannot be blank");
        if (baseUrl == null || baseUrl.isBlank()) throw new IllegalArgumentException("Base URL cannot be blank");
        if (startEndpoint == null || startEndpoint.isBlank())
            throw new IllegalArgumentException("Start endpoint cannot be blank");

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