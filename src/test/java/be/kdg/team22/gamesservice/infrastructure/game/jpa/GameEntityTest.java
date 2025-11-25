package be.kdg.team22.gamesservice.infrastructure.game.jpa;

import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameEntityTest {

    private Game domainGame() {
        return new Game(
                GameId.from(UUID.randomUUID()),
                "checkers",
                "http://localhost:9092",
                "/start"
        );
    }

    @Test
    void fromDomain_mapsAllFieldsCorrectly() {
        Game game = domainGame();

        GameEntity entity = GameEntity.fromDomain(game);

        assertThat(entity.id()).isEqualTo(game.id().value());
        assertThat(entity.name()).isEqualTo("checkers");
        assertThat(entity.baseUrl()).isEqualTo("http://localhost:9092");
        assertThat(entity.startEndpoint()).isEqualTo("/start");
    }

    @Test
    void toDomain_mapsAllFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        GameEntity entity = new GameEntity(
                id,
                "tic-tac-toe",
                "http://localhost:9091",
                "/start"
        );

        Game domain = entity.toDomain();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.name()).isEqualTo("tic-tac-toe");
        assertThat(domain.baseUrl()).isEqualTo("http://localhost:9091");
        assertThat(domain.startEndpoint()).isEqualTo("/start");
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
    }
}