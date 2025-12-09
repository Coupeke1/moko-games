package be.kdg.team22.gamesservice.infrastructure.game;

import be.kdg.team22.gamesservice.config.TestcontainersConfig;
import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import be.kdg.team22.gamesservice.infrastructure.game.jpa.GameEntity;
import be.kdg.team22.gamesservice.infrastructure.game.jpa.GameJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DbGameRepositoryTest {

    @Autowired
    private GameJpaRepository jpaRepo;

    private GameEntity createEntity(UUID id) {
        return new GameEntity(
                id,
                "engine-name",
                "http://localhost:9999",
                "/start",
                "/start",
                "/health",
                Instant.parse("2024-01-02T10:00:00Z"),
                true,
                "Title",
                "Description",
                "http://image",
                Instant.parse("2024-01-01T10:00:00Z"),
                Instant.parse("2024-01-02T10:00:00Z")
        );
    }

    @Test
    void saveAndLoadGameEntity() {
        UUID id = UUID.randomUUID();
        GameEntity entity = createEntity(id);

        jpaRepo.save(entity);

        Optional<GameEntity> loadedOpt = jpaRepo.findById(id);
        assertThat(loadedOpt).isPresent();

        GameEntity loaded = loadedOpt.get();

        assertThat(loaded.id()).isEqualTo(id);
        assertThat(loaded.name()).isEqualTo("engine-name");
        assertThat(loaded.baseUrl()).isEqualTo("http://localhost:9999");
        assertThat(loaded.startEndpoint()).isEqualTo("/start");
        assertThat(loaded.title()).isEqualTo("Title");
        assertThat(loaded.description()).isEqualTo("Description");
        assertThat(loaded.image()).isEqualTo("http://image");
    }

    @Test
    void fromDomain_createsMatchingEntity() {
        Game domain = new Game(
                GameId.from(UUID.randomUUID()),
                "engine",
                "http://localhost",
                "/play",
                "/play",
                "/health",
                Instant.parse("2024-01-01T12:00:00Z"),
                true,
                "T",
                "D",
                "http://img",
                Instant.parse("2024-01-01T10:00:00Z"),
                Instant.parse("2024-01-01T12:00:00Z")
        );

        GameEntity entity = GameEntity.fromDomain(domain);

        assertThat(entity.id()).isEqualTo(domain.id().value());
        assertThat(entity.name()).isEqualTo("engine");
        assertThat(entity.baseUrl()).isEqualTo("http://localhost");
        assertThat(entity.startEndpoint()).isEqualTo("/play");
        assertThat(entity.healthEndpoint()).isEqualTo("/health");
        assertThat(entity.healthy()).isEqualTo(true);
        assertThat(entity.title()).isEqualTo("T");
        assertThat(entity.description()).isEqualTo("D");
        assertThat(entity.image()).isEqualTo("http://img");
    }

    @Test
    void toDomain_restoresAccurately() {
        UUID id = UUID.randomUUID();
        GameEntity entity = createEntity(id);

        Game domain = entity.toDomain();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.name()).isEqualTo("engine-name");
        assertThat(domain.baseUrl()).isEqualTo("http://localhost:9999");
        assertThat(domain.startEndpoint()).isEqualTo("/start");
        assertThat(domain.healthEndpoint()).isEqualTo("/health");
        assertThat(domain.title()).isEqualTo("Title");
        assertThat(domain.description()).isEqualTo("Description");
        assertThat(domain.image()).isEqualTo("http://image");
    }
}