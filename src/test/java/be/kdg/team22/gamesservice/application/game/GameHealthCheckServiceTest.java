package be.kdg.team22.gamesservice.application.game;

import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import be.kdg.team22.gamesservice.domain.game.GameRepository;
import be.kdg.team22.gamesservice.infrastructure.game.health.GameHealthChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class GameHealthCheckServiceTest {

    private final GameRepository gameRepository = mock(GameRepository.class);
    private final GameHealthChecker gameHealthChecker = mock(GameHealthChecker.class);
    private final GameHealthCheckService service = new GameHealthCheckService(gameRepository, gameHealthChecker);

    private Game sampleGame(GameId id) {
        return new Game(
                id,
                "checkers",
                "http://localhost:8087",
                "http://localhost:3000",
                "/start",
                "/health",
                "Checkers",
                "A fun board game",
                "http://img"
        );
    }

    @Test
    @DisplayName("checkAllGamesHealth → no games → no operations")
    void checkAllGamesHealth_noGames() {
        when(gameRepository.findAll()).thenReturn(List.of());

        service.checkAllGamesHealth();

        verify(gameRepository).findAll();
        verifyNoInteractions(gameHealthChecker);
    }

    @Test
    @DisplayName("checkAllGamesHealth → single healthy game → updates status")
    void checkAllGamesHealth_singleHealthyGame() {
        GameId id = GameId.from(UUID.randomUUID());
        Game game = sampleGame(id);

        when(gameRepository.findAll()).thenReturn(List.of(game));
        when(gameHealthChecker.isHealthy(game)).thenReturn(true);

        service.checkAllGamesHealth();

        verify(gameRepository).findAll();
        verify(gameHealthChecker).isHealthy(game);
        verify(gameRepository).save(game);
    }

    @Test
    @DisplayName("checkAllGamesHealth → single unhealthy game → marks as unhealthy")
    void checkAllGamesHealth_singleUnhealthyGame() {
        GameId id = GameId.from(UUID.randomUUID());
        Game game = sampleGame(id);

        when(gameRepository.findAll()).thenReturn(List.of(game));
        when(gameHealthChecker.isHealthy(game)).thenReturn(false);

        service.checkAllGamesHealth();

        verify(gameRepository).findAll();
        verify(gameHealthChecker).isHealthy(game);
        verify(gameRepository).save(game);
    }

    @Test
    @DisplayName("checkAllGamesHealth → exception during health check → marks as unhealthy")
    void checkAllGamesHealth_exceptionDuringHealthCheck() {
        GameId id = GameId.from(UUID.randomUUID());
        Game game = sampleGame(id);

        when(gameRepository.findAll()).thenReturn(List.of(game));
        when(gameHealthChecker.isHealthy(game)).thenThrow(new RuntimeException("Health check failed"));

        service.checkAllGamesHealth();

        verify(gameRepository).findAll();
        verify(gameHealthChecker).isHealthy(game);
        verify(gameRepository).save(game);
    }

    @Test
    @DisplayName("checkAllGamesHealth → multiple games → checks all")
    void checkAllGamesHealth_multipleGames() {
        GameId id1 = GameId.from(UUID.randomUUID());
        GameId id2 = GameId.from(UUID.randomUUID());
        GameId id3 = GameId.from(UUID.randomUUID());

        Game game1 = sampleGame(id1);
        Game game2 = sampleGame(id2);
        Game game3 = sampleGame(id3);

        when(gameRepository.findAll()).thenReturn(List.of(game1, game2, game3));
        when(gameHealthChecker.isHealthy(game1)).thenReturn(true);
        when(gameHealthChecker.isHealthy(game2)).thenReturn(false);
        when(gameHealthChecker.isHealthy(game3)).thenReturn(true);

        service.checkAllGamesHealth();

        verify(gameRepository).findAll();
        verify(gameHealthChecker).isHealthy(game1);
        verify(gameHealthChecker).isHealthy(game2);
        verify(gameHealthChecker).isHealthy(game3);
        verify(gameRepository, times(3)).save(any(Game.class));
    }

    @Test
    @DisplayName("checkAllGamesHealth → mixed healthy and exceptions → handles all")
    void checkAllGamesHealth_mixedHealthyAndExceptions() {
        GameId id1 = GameId.from(UUID.randomUUID());
        GameId id2 = GameId.from(UUID.randomUUID());

        Game game1 = sampleGame(id1);
        Game game2 = sampleGame(id2);

        when(gameRepository.findAll()).thenReturn(List.of(game1, game2));
        when(gameHealthChecker.isHealthy(game1)).thenReturn(true);
        when(gameHealthChecker.isHealthy(game2)).thenThrow(new RuntimeException("Connection timeout"));

        service.checkAllGamesHealth();

        verify(gameRepository).findAll();
        verify(gameHealthChecker).isHealthy(game1);
        verify(gameHealthChecker).isHealthy(game2);
        verify(gameRepository, times(2)).save(any(Game.class));
    }
}
