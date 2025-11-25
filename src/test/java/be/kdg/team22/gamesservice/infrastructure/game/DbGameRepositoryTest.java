package be.kdg.team22.gamesservice.infrastructure.game;

import be.kdg.team22.gamesservice.config.TestcontainersConfig;
import be.kdg.team22.gamesservice.infrastructure.game.jpa.GameEntity;
import be.kdg.team22.gamesservice.infrastructure.game.jpa.GameJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DbGameRepositoryTest {

    @Autowired
    private GameJpaRepository jpaRepo;

    @Test
    void saveAndLoadGameEntity() {
        UUID id = UUID.randomUUID();

        GameEntity entity = new GameEntity(
                id,
                "tic-tac-toe",
                "http://localhost:9091",
                "/start"
        );

        jpaRepo.save(entity);

        Optional<GameEntity> loadedOpt = jpaRepo.findById(id);
        assertThat(loadedOpt).isPresent();

        GameEntity loaded = loadedOpt.get();

        assertThat(loaded.id()).isEqualTo(id);
        assertThat(loaded.name()).isEqualTo("tic-tac-toe");
        assertThat(loaded.baseUrl()).isEqualTo("http://localhost:9091");
        assertThat(loaded.startEndpoint()).isEqualTo("/start");
    }
}
