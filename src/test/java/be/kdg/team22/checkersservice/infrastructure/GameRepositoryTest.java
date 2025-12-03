package be.kdg.team22.checkersservice.infrastructure;

import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.game.GameId;
import be.kdg.team22.checkersservice.infrastructure.game.memory.InMemoryGameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameRepositoryTest {

    private InMemoryGameRepository repository;
    private Game mockGame;
    private GameId mockGameId;

    @BeforeEach
    void setUp() {
        repository = new InMemoryGameRepository();
        mockGame = mock(Game.class);
        mockGameId = mock(GameId.class);

        lenient().when(mockGame.id()).thenReturn(mockGameId);
    }

    @Test
    void saveShouldStoreGame() {
        repository.save(mockGame);

        Optional<Game> retrievedGame = repository.findById(mockGameId);
        assertThat(retrievedGame).isPresent();
        assertThat(retrievedGame.get()).isEqualTo(mockGame);
    }

    @Test
    void saveShouldUpdateExistingGame() {
        Game updatedGame = mock(Game.class);
        when(updatedGame.id()).thenReturn(mockGameId);
        repository.save(mockGame);

        repository.save(updatedGame);

        Optional<Game> retrievedGame = repository.findById(mockGameId);
        assertThat(retrievedGame).isPresent();
        assertThat(retrievedGame.get()).isEqualTo(updatedGame);
        assertThat(retrievedGame.get()).isNotEqualTo(mockGame);
    }

    @Test
    void deleteShouldRemoveGame() {
        repository.save(mockGame);
        repository.delete(mockGameId);

        Optional<Game> retrievedGame = repository.findById(mockGameId);
        assertThat(retrievedGame).isEmpty();
    }

    @Test
    void deleteWhenGameDoesNotExistShouldDoNothing() {
        repository.delete(mockGameId);

        Optional<Game> retrievedGame = repository.findById(mockGameId);
        assertThat(retrievedGame).isEmpty();
    }

    @Test
    void findById_whenGameExists_shouldReturnGame() {
        repository.save(mockGame);

        Optional<Game> retrievedGame = repository.findById(mockGameId);
        assertThat(retrievedGame).isPresent();
        assertThat(retrievedGame.get()).isEqualTo(mockGame);
    }

    @Test
    void findById_whenGameDoesNotExist_shouldReturnEmptyOptional() {
        Optional<Game> retrievedGame = repository.findById(mockGameId);
        assertThat(retrievedGame).isEmpty();
    }
}
