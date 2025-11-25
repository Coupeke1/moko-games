package be.kdg.team22.gamesservice.domain.game;

import be.kdg.team22.gamesservice.domain.game.exceptions.GameBaseUrlInvalidException;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameIdNullException;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameNameInvalidException;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameStartEndpointInvalidException;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;

@AggregateRoot
public class Game {

    private final GameId id;
    private final Instant createdAt;
    private String name;
    private String baseUrl;
    private String startEndpoint;
    private Instant updatedAt;

    public Game(
            final GameId id,
            final String name,
            final String baseUrl,
            final String startEndpoint,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        validate(id, name, baseUrl, startEndpoint);

        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.startEndpoint = startEndpoint;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Game(
            final GameId id,
            final String name,
            final String baseUrl,
            final String startEndpoint
    ) {
        validate(id, name, baseUrl, startEndpoint);

        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.startEndpoint = startEndpoint;

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    private void validate(
            final GameId id,
            final String name,
            final String baseUrl,
            final String startEndpoint
    ) {
        if (id == null) throw new GameIdNullException();
        if (name == null || name.isBlank()) throw new GameNameInvalidException();
        if (baseUrl == null || baseUrl.isBlank()) throw new GameBaseUrlInvalidException();
        if (startEndpoint == null || startEndpoint.isBlank()) throw new GameStartEndpointInvalidException();
    }

    public void rename(final String newName) {
        if (newName == null || newName.isBlank()) throw new GameNameInvalidException();
        this.name = newName;
        this.updatedAt = Instant.now();
    }

    public void changeBaseUrl(final String newUrl) {
        if (newUrl == null || newUrl.isBlank()) throw new GameBaseUrlInvalidException();
        this.baseUrl = newUrl;
        this.updatedAt = Instant.now();
    }

    public void changeStartEndpoint(final String newEndpoint) {
        if (newEndpoint == null || newEndpoint.isBlank()) throw new GameStartEndpointInvalidException();
        this.startEndpoint = newEndpoint;
        this.updatedAt = Instant.now();
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

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}