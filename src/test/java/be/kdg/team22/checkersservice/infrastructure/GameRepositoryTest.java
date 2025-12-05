package be.kdg.team22.checkersservice.infrastructure;

import be.kdg.team22.checkersservice.config.TestcontainersConfig;
import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.move.KingMovementMode;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import be.kdg.team22.checkersservice.infrastructure.game.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Import(TestcontainersConfig.class)
public class GameRepositoryTest {

    @Autowired
    private GameRepository repository;

    private Game createTestGame() {
        return Game.create(
                List.of(PlayerId.create("00000000-0000-0000-0000-000000000000"),
                        PlayerId.create("00000000-0000-0000-0000-000000000001")),
                false,
                KingMovementMode.FLYING
        );
    }

    @Test
    void saveShouldStoreGame() {
        Game game = createTestGame();

        repository.save(game);

        Optional<Game> retrievedGame = repository.findById(game.id());
        assertThat(retrievedGame).isPresent();
        assertThat(retrievedGame.get().id()).isEqualTo(game.id());
    }

    @Test
    void deleteShouldRemoveGame() {
        Game game = createTestGame();
        repository.save(game);
        repository.delete(game.id());

        Optional<Game> retrievedGame = repository.findById(game.id());
        assertThat(retrievedGame).isEmpty();
    }
}
