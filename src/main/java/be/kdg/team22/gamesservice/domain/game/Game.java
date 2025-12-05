package be.kdg.team22.gamesservice.domain.game;

import be.kdg.team22.gamesservice.api.game.models.RegisterGameRequest;
import be.kdg.team22.gamesservice.domain.game.exceptions.*;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.math.BigDecimal;
import java.time.Instant;

@AggregateRoot
public class Game {

    private final GameId id;
    private final Instant createdAt;
    private Instant updatedAt;

    // Engine data
    private String name;
    private String baseUrl;
    private String frontendUrl;
    private String startEndpoint;

    // frontend metadata
    private String title;
    private String description;
    private String image;

    public Game(
            final GameId id,
            final String name,
            final String baseUrl,
            final String frontendUrl,
            final String startEndpoint,
            final String title,
            final String description,
            final String image,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        validate(id, name, baseUrl, frontendUrl, startEndpoint, title, description, image);

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

    public Game(
            final GameId id,
            final String name,
            final String baseUrl,
            final String frontendUrl,
            final String startEndpoint,
            final String title,
            final String description,
            final String image
    ) {
        validate(id, name, baseUrl, frontendUrl, startEndpoint, title, description, image);

        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.startEndpoint = startEndpoint;
        this.frontendUrl = frontendUrl;

        this.title = title;
        this.description = description;
        this.image = image;

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    private void validate(
            final GameId id,
            final String name,
            final String baseUrl,
            final String frontendUrl,
            final String startEndpoint,
            final String title,
            final String description,
            final String image
    ) {
        if (id == null) throw new GameIdNullException();
        if (name == null || name.isBlank()) throw new GameNameInvalidException();
        if (baseUrl == null || baseUrl.isBlank()) throw new GameBaseUrlInvalidException();
        if (frontendUrl == null || frontendUrl.isBlank()) throw new GameFrontendUrlInvalidException();
        if (startEndpoint == null || startEndpoint.isBlank()) throw new GameStartEndpointInvalidException();
        validateMetaData(title, description, image);
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

    public void updateStoreMetadata(
            final String title,
            final String description,
            final String image
    ) {
        validateMetaData(title, description, image);

        this.title = title;
        this.description = description;
        this.image = image;
        this.updatedAt = Instant.now();
    }

    private void validateMetaData(
            final String title,
            final String description,
            final String image
    ) {
        if (title == null || title.isBlank()) {
            throw GameMetadataException.invalidTitle();
        }
        if (description == null || description.isBlank()) {
            throw GameMetadataException.invalidDescription();
        }
        if (image == null || image.isBlank()) {
            throw GameMetadataException.invalidImage();
        }
    }

    public static Game register(RegisterGameRequest request) {
        return new Game(
                GameId.create(),
                request.name(),
                request.backendUrl(),
                request.frontendUrl(),
                request.startEndpoint(),
                request.title(),
                request.description(),
                request.image()
        );
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

    public String frontendUrl() {
        return frontendUrl;
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