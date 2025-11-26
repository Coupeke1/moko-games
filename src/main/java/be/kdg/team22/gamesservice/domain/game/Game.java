package be.kdg.team22.gamesservice.domain.game;

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
    private String startEndpoint;

    // frontend metadata
    private String title;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String storeUrl;

    public Game(
            final GameId id,
            final String name,
            final String baseUrl,
            final String startEndpoint,
            final String title,
            final String description,
            final BigDecimal price,
            final String imageUrl,
            final String storeUrl,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        validate(id, name, baseUrl, startEndpoint, title, description, price, imageUrl);

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

    public Game(
            final GameId id,
            final String name,
            final String baseUrl,
            final String startEndpoint,
            final String title,
            final String description,
            final BigDecimal price,
            final String imageUrl,
            final String storeUrl
    ) {
        validate(id, name, baseUrl, startEndpoint, title, description, price, imageUrl);

        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.startEndpoint = startEndpoint;

        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.storeUrl = storeUrl;

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    private void validate(
            final GameId id,
            final String name,
            final String baseUrl,
            final String startEndpoint,
            final String title,
            final String description,
            final BigDecimal price,
            final String imageUrl
    ) {
        if (id == null) throw new GameIdNullException();
        if (name == null || name.isBlank()) throw new GameNameInvalidException();
        if (baseUrl == null || baseUrl.isBlank()) throw new GameBaseUrlInvalidException();
        if (startEndpoint == null || startEndpoint.isBlank()) throw new GameStartEndpointInvalidException();

        validateMetaData(title, description, price, imageUrl);
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
            final BigDecimal price,
            final String imageUrl,
            final String storeUrl
    ) {
        validateMetaData(title, description, price, imageUrl);

        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.storeUrl = storeUrl;
        this.updatedAt = Instant.now();
    }

    private void validateMetaData(
            final String title,
            final String description,
            final BigDecimal price,
            final String imageUrl
    ) {
        if (title == null || title.isBlank()) {
            throw GameMetadataException.invalidTitle();
        }
        if (description == null || description.isBlank()) {
            throw GameMetadataException.invalidDescription();
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw GameMetadataException.invalidPrice();
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw GameMetadataException.invalidImageUrl();
        }
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

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public BigDecimal price() {
        return price;
    }

    public String imageUrl() {
        return imageUrl;
    }

    public String storeUrl() {
        return storeUrl;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}