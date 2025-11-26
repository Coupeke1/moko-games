package be.kdg.team22.gamesservice.infrastructure.game.jpa;

import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameEntityTest {

    private Game domainGame() {
        return new Game(
                GameId.from(UUID.randomUUID()),
                "checkers",
                "http://localhost:9092",
                "/start",
                "Checkers",
                "Strategy board game",
                new BigDecimal("10.00"),
                "http://image",
                "http://store",
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z")
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
        assertThat(entity.price()).isEqualTo(game.price());
        assertThat(entity.imageUrl()).isEqualTo(game.imageUrl());
        assertThat(entity.storeUrl()).isEqualTo(game.storeUrl());
        assertThat(entity.createdAt()).isEqualTo(game.createdAt());
        assertThat(entity.updatedAt()).isEqualTo(game.updatedAt());
    }

    @Test
    void toDomain_mapsAllFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        Instant created = Instant.parse("2024-01-01T12:00:00Z");
        Instant updated = Instant.parse("2024-01-02T12:00:00Z");

        GameEntity entity = new GameEntity(
                id,
                "tic-tac-toe",
                "http://localhost:9091",
                "/start",
                "Tic Tac Toe",
                "Classic game",
                new BigDecimal("5.00"),
                "http://image",
                "http://store",
                created,
                updated
        );

        Game domain = entity.toDomain();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.name()).isEqualTo("tic-tac-toe");
        assertThat(domain.baseUrl()).isEqualTo("http://localhost:9091");
        assertThat(domain.startEndpoint()).isEqualTo("/start");
        assertThat(domain.title()).isEqualTo("Tic Tac Toe");
        assertThat(domain.description()).isEqualTo("Classic game");
        assertThat(domain.price()).isEqualTo(new BigDecimal("5.00"));
        assertThat(domain.imageUrl()).isEqualTo("http://image");
        assertThat(domain.storeUrl()).isEqualTo("http://store");
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
        assertThat(converted.description()).isEqualTo(original.description());
        assertThat(converted.price()).isEqualTo(original.price());
        assertThat(converted.imageUrl()).isEqualTo(original.imageUrl());
        assertThat(converted.storeUrl()).isEqualTo(original.storeUrl());
        assertThat(converted.createdAt()).isEqualTo(original.createdAt());
        assertThat(converted.updatedAt()).isEqualTo(original.updatedAt());
    }
}