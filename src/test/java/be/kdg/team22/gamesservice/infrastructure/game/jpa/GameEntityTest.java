package be.kdg.team22.gamesservice.infrastructure.game.jpa;

import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import be.kdg.team22.gamesservice.domain.game.settings.GameSettingsDefinition;
import be.kdg.team22.gamesservice.domain.game.settings.SettingDefinition;
import be.kdg.team22.gamesservice.domain.game.settings.SettingType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameEntityTest {
    private GameSettingsDefinition defaultSettings() {
        return new GameSettingsDefinition(
                List.of(
                        new SettingDefinition(
                                "dummy",
                                SettingType.STRING,
                                false,
                                null,
                                null,
                                null
                        )
                )
        );
    }

    private Game domainGame() {
        return new Game(
                GameId.from(UUID.randomUUID()),
                "checkers",
                "http://localhost:9092",
                "/start",
                "/start",
                "/health",
                Instant.parse("2024-01-02T00:00:00Z"),
                true,
                "Checkers",
                "Strategy board game",
                "http://image",
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z"),
                defaultSettings()
        );
    }

    @Test
    void fromDomain_mapsAllFieldsCorrectly() {
        Game game = domainGame();

        GameEntity entity = GameEntity.fromDomain(game);

        assertThat(entity.id()).isEqualTo(game.id().value());
        assertThat(entity.name()).isEqualTo(game.name());
        assertThat(entity.baseUrl()).isEqualTo(game.baseUrl());
        assertThat(entity.startEndpoint()).isEqualTo(game.startEndpoint());
        assertThat(entity.title()).isEqualTo(game.title());
        assertThat(entity.description()).isEqualTo(game.description());
        assertThat(entity.image()).isEqualTo(game.image());
        assertThat(entity.createdAt()).isEqualTo(game.createdAt());
        assertThat(entity.updatedAt()).isEqualTo(game.updatedAt());
    }

    @Test
    void toDomain_mapsAllFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        Instant created = Instant.parse("2024-01-01T12:00:00Z");
        Instant updated = Instant.parse("2024-01-02T12:00:00Z");
        Instant healthCheck = Instant.parse("2024-01-02T12:00:00Z");

        GameEntity entity = new GameEntity(
                id,
                "tic-tac-toe",
                "http://localhost:9091",
                "/start",
                "/start",
                "/health",
                healthCheck,
                true,
                "Tic Tac Toe",
                "Classic game",
                "http://image",
                defaultSettings(),
                created,
                updated
        );

        Game domain = entity.toDomain();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.name()).isEqualTo("tic-tac-toe");
        assertThat(domain.baseUrl()).isEqualTo("http://localhost:9091");
        assertThat(domain.startEndpoint()).isEqualTo("/start");
        assertThat(domain.healthEndpoint()).isEqualTo("/health");
        assertThat(domain.lastHealthCheck()).isEqualTo(healthCheck);
        assertThat(domain.healthy()).isTrue();
        assertThat(domain.title()).isEqualTo("Tic Tac Toe");
        assertThat(domain.description()).isEqualTo("Classic game");
        assertThat(domain.image()).isEqualTo("http://image");
        assertThat(domain.createdAt()).isEqualTo(created);
        assertThat(domain.updatedAt()).isEqualTo(updated);
    }

    @Test
    void roundTrip_conversionFromDomainAndBackIsConsistent() {
        Game original = domainGame();

        GameEntity entity = GameEntity.fromDomain(original);
        Game converted = entity.toDomain();

        assertThat(converted.id()).isEqualTo(original.id());
        assertThat(converted.name()).isEqualTo(original.name());
        assertThat(converted.baseUrl()).isEqualTo(original.baseUrl());
        assertThat(converted.startEndpoint()).isEqualTo(original.startEndpoint());
        assertThat(converted.title()).isEqualTo(original.title());
        assertThat(converted.healthEndpoint()).isEqualTo(original.healthEndpoint());
        assertThat(converted.lastHealthCheck()).isEqualTo(original.lastHealthCheck());
        assertThat(converted.healthy()).isEqualTo(original.healthy());
        assertThat(converted.description()).isEqualTo(original.description());
        assertThat(converted.image()).isEqualTo(original.image());
        assertThat(converted.createdAt()).isEqualTo(original.createdAt());
        assertThat(converted.updatedAt()).isEqualTo(original.updatedAt());
    }
}