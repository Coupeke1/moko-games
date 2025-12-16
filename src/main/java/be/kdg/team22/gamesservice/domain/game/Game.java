package be.kdg.team22.gamesservice.domain.game;

import be.kdg.team22.gamesservice.api.game.models.RegisterAchievementRequest;
import be.kdg.team22.gamesservice.api.game.models.RegisterGameRequest;
import be.kdg.team22.gamesservice.domain.game.exceptions.*;
import be.kdg.team22.gamesservice.domain.game.settings.GameSettingsDefinition;
import be.kdg.team22.gamesservice.domain.game.settings.GameSettingsValidator;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@AggregateRoot
public class Game {

    private final GameId id;
    private final Instant createdAt;
    private final Set<Achievement> achievements = new HashSet<>();
    private Instant updatedAt;
    // Engine data
    private String name;
    private String baseUrl;
    private String frontendUrl;
    private String startEndpoint;
    private final GameSettingsDefinition settingsDefinition;
    private String healthEndpoint;
    private Instant lastHealthCheck;
    private boolean healthy;
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
            final String healthEndpoint,
            final Instant lastHealthCheck,
            final boolean healthy,
            final String title,
            final String description,
            final String image,
            final Instant createdAt,
            final Instant updatedAt,
            final GameSettingsDefinition settingsDefinition
    ) {
        validate(id, name, baseUrl, frontendUrl, startEndpoint, healthEndpoint, title, description, image);

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
        this.settingsDefinition = settingsDefinition;

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Game(
            final GameId id,
            final String name,
            final String baseUrl,
            final String frontendUrl,
            final String startEndpoint,
            final String healthEndpoint,
            final String title,
            final String description,
            final String image,
            final GameSettingsDefinition settingsDefinition
    ) {
        validate(id, name, baseUrl, frontendUrl, startEndpoint, healthEndpoint, title, description, image);

        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.startEndpoint = startEndpoint;
        this.frontendUrl = frontendUrl;
        this.healthEndpoint = healthEndpoint;
        this.healthy = false;

        this.title = title;
        this.description = description;
        this.image = image;

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.settingsDefinition = settingsDefinition;
    }

    public static Game register(final RegisterGameRequest request) {
        Game game = new Game(
                GameId.create(),
                request.name(),
                request.backendUrl(),
                request.frontendUrl(),
                request.startEndpoint(),
                request.healthEndpoint(),
                request.title(),
                request.description(),
                request.image(),
                request.settingsDefinition()

        );

        if (request.achievements() == null) return game;

        List<Achievement> achievements = new ArrayList<>();
        request.achievements().forEach(achievement -> achievements.add(new Achievement(
                AchievementKey.fromString(achievement.key()),
                achievement.name(),
                achievement.description(),
                achievement.levels()
        )));
        game.addAchievements(achievements);

        return game;
    }

    private void validate(
            final GameId id,
            final String name,
            final String baseUrl,
            final String frontendUrl,
            final String startEndpoint,
            final String healthEndpoint,
            final String title,
            final String description,
            final String image
    ) {
        if (id == null) throw new GameIdNullException();
        validate(name, baseUrl, frontendUrl, startEndpoint, healthEndpoint, title, description, image);
    }

    private void validate(
            final String name,
            final String baseUrl,
            final String frontendUrl,
            final String startEndpoint,
            final String healthEndpoint,
            final String title,
            final String description,
            final String image
    ) {
        if (name == null || name.isBlank()) throw new GameNameInvalidException();
        if (baseUrl == null || baseUrl.isBlank()) throw new GameBaseUrlInvalidException();
        if (frontendUrl == null || frontendUrl.isBlank()) throw new GameFrontendUrlInvalidException();
        if (startEndpoint == null || startEndpoint.isBlank()) throw new GameStartEndpointInvalidException();
        if (healthEndpoint == null || healthEndpoint.isBlank()) throw new GameHealthEndpointInvalidException();
        validateMetaData(title, description, image);
    }

    private void validate(RegisterGameRequest request) {
        validate(request.name(), request.backendUrl(), request.frontendUrl(), request.startEndpoint(), request.healthEndpoint(), request.title(), request.description(), request.image());
    }

    public void addAchievement(final Achievement achievement) {
        boolean added = this.achievements.add(achievement);
        if (!added) {
            throw new achievementKeyAlreadyExistsException(achievement.key().key());
        }
        this.updatedAt = Instant.now();
    }

    public void addAchievements(final List<Achievement> achievements) {
        for (final Achievement achievement : achievements) {
            addAchievement(achievement);
        }
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

    public void update(final RegisterGameRequest request) {
        validate(request);
        this.name = request.name();
        this.baseUrl = request.backendUrl();
        this.frontendUrl = request.frontendUrl();
        this.startEndpoint = request.startEndpoint();
        this.healthEndpoint = request.healthEndpoint();
        this.title = request.title();
        this.description = request.description();
        this.image = request.image();

        syncAchievement(request);

        this.updatedAt = Instant.now();
    }

    private void syncAchievement(final RegisterGameRequest request) {
        if (request.achievements() == null) {
            this.achievements.clear();
            return;
        }

        Map<AchievementKey, RegisterAchievementRequest> incoming =
                request.achievements().stream()
                        .collect(Collectors.toMap(
                                a -> AchievementKey.fromString(a.key()),
                                a -> a
                        ));

        Set<Achievement> toRemove = new HashSet<>();

        for (Achievement existing : this.achievements) {
            RegisterAchievementRequest match = incoming.get(existing.key());
            if (match == null) toRemove.add(existing);
            else {
                existing.update(match.name(), match.description(), match.levels());
                incoming.remove(existing.key());
            }
        }

        this.achievements.removeAll(toRemove);

        incoming.values().forEach(req ->
                this.achievements.add(new Achievement(
                        AchievementKey.fromString(req.key()),
                        req.name(),
                        req.description(),
                        req.levels()
                ))
        );
    }

    public void updateHealthStatus(final boolean isHealthy) {
        this.healthy = isHealthy;
        this.lastHealthCheck = Instant.now();
    }

    public GameId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Set<Achievement> achievements() {
        return achievements;
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

    public String healthEndpoint() {
        return healthEndpoint;
    }

    public Instant lastHealthCheck() {
        return lastHealthCheck;
    }

    public GameSettingsDefinition settingsDefinition() {
        return settingsDefinition;
    }

    public boolean healthy() {
        return healthy;
    }

    public Map<String, Object> validateSettings(Map<String, Object> settings) {
        return GameSettingsValidator.resolveAndValidate(settingsDefinition, settings);
    }
}