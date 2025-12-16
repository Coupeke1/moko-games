package be.kdg.team22.gamesservice.application.game;

import be.kdg.team22.gamesservice.api.game.models.CheckersSettingsModel;
import be.kdg.team22.gamesservice.api.game.models.GameSettingsModel;
import be.kdg.team22.gamesservice.api.game.models.RegisterGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameResponseModel;
import be.kdg.team22.gamesservice.domain.game.Achievement;
import be.kdg.team22.gamesservice.domain.game.AchievementKey;
import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameCategory;
import be.kdg.team22.gamesservice.domain.game.GameId;
import be.kdg.team22.gamesservice.domain.game.GameRepository;
import be.kdg.team22.gamesservice.domain.game.exceptions.AchievementNotFoundException;
import be.kdg.team22.gamesservice.domain.game.exceptions.DuplicateGameNameException;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameNotFoundException;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameUnhealthyException;
import be.kdg.team22.gamesservice.domain.game.exceptions.InvalidGameConfigurationException;
import be.kdg.team22.gamesservice.domain.game.exceptions.PlayersListEmptyException;
import be.kdg.team22.gamesservice.infrastructure.game.engine.ExternalGamesRepository;
import be.kdg.team22.gamesservice.infrastructure.game.health.GameHealthChecker;
import be.kdg.team22.gamesservice.infrastructure.store.ExternalStoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private final GameRepository gameRepository = mock(GameRepository.class);
    private final GameHealthChecker gameHealthChecker = mock(GameHealthChecker.class);
    private final ExternalStoreRepository externalStoreRepository = mock(ExternalStoreRepository.class);
    private final ExternalGamesRepository engine = mock(ExternalGamesRepository.class);
    private final GameService service = new GameService(gameRepository, gameHealthChecker, externalStoreRepository, engine);

    private Game sampleGame(GameId id) {
        return new Game(
                id,
                "checkers",
                "http://localhost:8087",
                "/start",
                "/start",
                "/health",
                "Checkers",
                "A fun board game",
                "http://img"
        );
    }

    private StartGameRequest createRequest(GameSettingsModel settings, boolean ai) {
        return new StartGameRequest(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                List.of(
                        UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                        UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
                ),
                settings,
                ai
        );
    }

    private Jwt tokenFromUuid(UUID id){
        return Jwt.withTokenValue("token-" + id)
                .header("alg", "none")
                .subject(id.toString())
                .claim("preferred_username", "mathias")
                .claim("email", "m@a")
                .build();
    }

    @Test
    @DisplayName("startGame → happy path")
    void startGame_happyPath() {
        GameId gameId = GameId.from(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        Game game = sampleGame(gameId);

        StartGameRequest request = createRequest(new CheckersSettingsModel(8, true), true);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        UUID instanceId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        when(engine.startExternalGame(game, request, tokenFromUuid(request.players().getFirst()))).thenReturn(instanceId);

        StartGameResponseModel response = service.startGame(request, tokenFromUuid(request.players().getFirst()));

        assertThat(response.gameInstanceId()).isEqualTo(instanceId);

        verify(gameRepository).findById(gameId);
        verify(engine).startExternalGame(game, request, tokenFromUuid(request.players().getFirst()));
    }

    @Test
    @DisplayName("startGame → empty players → PlayersListEmptyException")
    void startGame_emptyPlayers() {
        StartGameRequest request = new StartGameRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(),
                new CheckersSettingsModel(8, true),
                false
        );

        assertThatThrownBy(() -> service.startGame(request, null))
                .isInstanceOf(PlayersListEmptyException.class);

        verifyNoInteractions(gameRepository, engine);
    }

    @Test
    @DisplayName("startGame → null settings → InvalidGameConfigurationException")
    void startGame_nullSettings() {
        StartGameRequest request = new StartGameRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(UUID.randomUUID()),
                null,
                false
        );

        assertThatThrownBy(() -> service.startGame(request, tokenFromUuid(request.players().getFirst())))
                .isInstanceOf(InvalidGameConfigurationException.class)
                .hasMessageContaining("Game settings cannot be null");

        verifyNoInteractions(gameRepository, engine);
    }

    @Test
    @DisplayName("startGame → game not found → GameNotFoundException")
    void startGame_gameNotFound() {
        GameId id = GameId.from(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        StartGameRequest request = createRequest(new CheckersSettingsModel(8, true), false);

        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.startGame(request, tokenFromUuid(request.players().getFirst())))
                .isInstanceOf(GameNotFoundException.class);

        verify(gameRepository).findById(id);
        verifyNoInteractions(engine);
    }

    @Test
    @DisplayName("findById → returns entity when present")
    void findById_found() {
        GameId id = GameId.from(UUID.randomUUID());
        Game game = sampleGame(id);

        when(gameRepository.findById(id)).thenReturn(Optional.of(game));

        Game result = service.findById(id);

        assertThat(result).isEqualTo(game);
        verify(gameRepository).findById(id);
    }

    @Test
    @DisplayName("findById → throws when missing")
    void findById_notFound() {
        GameId id = GameId.from(UUID.randomUUID());

        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(GameNotFoundException.class);

        verify(gameRepository).findById(id);
    }

    @Test
    @DisplayName("findAll → returns list from repository")
    void findAll_returnsList() {
        Game game1 = sampleGame(GameId.from(UUID.randomUUID()));
        Game game2 = sampleGame(GameId.from(UUID.randomUUID()));

        when(gameRepository.findAll()).thenReturn(List.of(game1, game2));

        List<Game> result = service.findAll();

        assertThat(result).containsExactly(game1, game2);
        verify(gameRepository).findAll();
    }

    @Test
    @DisplayName("register → happy path → creates and saves game")
    void create_happyPath() {
        RegisterGameRequest request = new RegisterGameRequest(
                "Checkers",
                "http://localhost:8087",
                "http://localhost:3000",
                "/start",
                "/health",
                "Checkers",
                "A fun board game",
                "http://img",
                BigDecimal.valueOf(25),
                GameCategory.PARTY,
                List.of()
        );

        when(gameRepository.findByName("Checkers")).thenReturn(Optional.empty());
        when(gameHealthChecker.isHealthy(request)).thenReturn(true);

        Game result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Checkers");
        assertThat(result.baseUrl()).isEqualTo("http://localhost:8087");
        assertThat(result.frontendUrl()).isEqualTo("http://localhost:3000");
        assertThat(result.startEndpoint()).isEqualTo("/start");
        assertThat(result.healthEndpoint()).isEqualTo("/health");
        assertThat(result.title()).isEqualTo("Checkers");
        assertThat(result.description()).isEqualTo("A fun board game");
        assertThat(result.image()).isEqualTo("http://img");
        assertThat(result.healthy()).isTrue();

        verify(gameRepository).findByName("Checkers");
        verify(gameHealthChecker).isHealthy(request);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("register → duplicate game name → throws DuplicateGameNameException")
    void create_duplicateGameName() {
        RegisterGameRequest request = new RegisterGameRequest(
                "Checkers",
                "http://localhost:8087",
                "http://localhost:3000",
                "/start",
                "/health",
                "Checkers",
                "A fun board game",
                "http://img",
                BigDecimal.valueOf(25),
                GameCategory.PARTY,
                List.of()
        );

        GameId existingId = GameId.from(UUID.randomUUID());
        Game existingGame = sampleGame(existingId);

        when(gameRepository.findByName("Checkers")).thenReturn(Optional.of(existingGame));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateGameNameException.class);

        verify(gameRepository).findByName("Checkers");
        verifyNoInteractions(gameHealthChecker);
        verify(gameRepository, never()).save(any());
    }

    @Test
    @DisplayName("register → unhealthy game → throws GameUnhealthyException")
    void create_unhealthyGame() {
        RegisterGameRequest request = new RegisterGameRequest(
                "Checkers",
                "http://localhost:8087",
                "http://localhost:3000",
                "/start",
                "/health",
                "Checkers",
                "A fun board game",
                "http://img",
                BigDecimal.valueOf(25),
                GameCategory.PARTY,
                List.of()
        );

        when(gameRepository.findByName("Checkers")).thenReturn(Optional.empty());
        when(gameHealthChecker.isHealthy(request)).thenReturn(false);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(GameUnhealthyException.class);

        verify(gameRepository).findByName("Checkers");
        verify(gameHealthChecker).isHealthy(request);
        verify(gameRepository, never()).save(any());
    }

    @Test
    @DisplayName("register → health check throws exception → propagates exception")
    void create_healthCheckException() {
        RegisterGameRequest request = new RegisterGameRequest(
                "Checkers",
                "http://localhost:8087",
                "http://localhost:3000",
                "/start",
                "/health",
                "Checkers",
                "A fun board game",
                "http://img",
                BigDecimal.valueOf(25),
                GameCategory.PARTY,
                List.of()
        );

        when(gameRepository.findByName("Checkers")).thenReturn(Optional.empty());
        when(gameHealthChecker.isHealthy(request)).thenThrow(new RuntimeException("Connection timeout"));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Connection timeout");

        verify(gameRepository).findByName("Checkers");
        verify(gameHealthChecker).isHealthy(request);
        verify(gameRepository, never()).save(any());
    }

    @Test
    @DisplayName("register → multiple games with same name → handles correctly")
    void create_multipleGamesCheckUniqueness() {
        RegisterGameRequest request1 = new RegisterGameRequest(
                "TicTacToe",
                "http://localhost:8088",
                "http://localhost:3001",
                "/start",
                "/health",
                "TicTacToe",
                "A classic game",
                "http://img",
                BigDecimal.valueOf(25),
                GameCategory.PARTY,
                List.of()
        );

        when(gameRepository.findByName("TicTacToe")).thenReturn(Optional.empty());
        when(gameHealthChecker.isHealthy(request1)).thenReturn(true);

        Game registeredGame = service.create(request1);

        assertThat(registeredGame.name()).isEqualTo("TicTacToe");

        RegisterGameRequest request2 = new RegisterGameRequest(
                "TicTacToe",
                "http://localhost:8089",
                "http://localhost:3002",
                "/start",
                "/health",
                "TicTacToe",
                "A classic game",
                "http://img",
                BigDecimal.valueOf(25),
                GameCategory.PARTY,
                List.of()
        );

        when(gameRepository.findByName("TicTacToe")).thenReturn(Optional.of(registeredGame));

        assertThatThrownBy(() -> service.create(request2))
                .isInstanceOf(DuplicateGameNameException.class);

        verify(gameRepository, times(2)).findByName("TicTacToe");
    }

    @Test
    @DisplayName("register → happy path → updates game")
    void register_happyPath() {
        Game existingGame = sampleGame(GameId.from(UUID.randomUUID()));

        RegisterGameRequest request = new RegisterGameRequest(
                "UpdatedCheckers",
                "http://localhost:8088",
                "http://localhost:3001",
                "/newStart",
                "/newHealth",
                "Updated Checkers",
                "An updated game",
                "http://newImg",
                BigDecimal.valueOf(30),
                GameCategory.STRATEGY,
                List.of()
        );

        when(gameRepository.findByName("checkers")).thenReturn(Optional.of(existingGame));
        when(gameRepository.findByName("UpdatedCheckers")).thenReturn(Optional.empty());
        when(gameHealthChecker.isHealthy(request)).thenReturn(true);

        Game result = service.register("checkers", request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("UpdatedCheckers");
        assertThat(result.baseUrl()).isEqualTo("http://localhost:8088");
        assertThat(result.frontendUrl()).isEqualTo("http://localhost:3001");
        assertThat(result.startEndpoint()).isEqualTo("/newStart");
        assertThat(result.healthEndpoint()).isEqualTo("/newHealth");
        assertThat(result.title()).isEqualTo("Updated Checkers");
        assertThat(result.description()).isEqualTo("An updated game");
        assertThat(result.image()).isEqualTo("http://newImg");
        assertThat(result.healthy()).isTrue();

        verify(gameRepository).findByName("checkers");
        verify(gameRepository).findByName("UpdatedCheckers");
        verify(gameHealthChecker).isHealthy(request);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("register → game not found → creates new game")
    void register_gameNotFound() {
        RegisterGameRequest request = new RegisterGameRequest(
                "NewGame",
                "http://localhost:8088",
                "http://localhost:3001",
                "/start",
                "/health",
                "New Game",
                "A new game",
                "http://newImg",
                BigDecimal.valueOf(30),
                GameCategory.STRATEGY,
                List.of()
        );

        when(gameRepository.findByName("NonExistent")).thenReturn(Optional.empty());
        when(gameHealthChecker.isHealthy(request)).thenReturn(true);

        Game result = service.register("NonExistent", request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("NewGame");

        verify(gameRepository).findByName("NonExistent");
        verify(gameHealthChecker).isHealthy(request);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("register → duplicate name (different from current) → throws DuplicateGameNameException")
    void register_duplicateGameName() {
        Game existingGame = sampleGame(GameId.from(UUID.randomUUID()));

        RegisterGameRequest request = new RegisterGameRequest(
                "DifferentName",
                "http://localhost:8088",
                "http://localhost:3001",
                "/start",
                "/health",
                "Different Title",
                "Different game",
                "http://newImg",
                BigDecimal.valueOf(30),
                GameCategory.STRATEGY,
                List.of()
        );

        GameId otherId = GameId.from(UUID.randomUUID());
        Game otherGameWithName = sampleGame(otherId);

        when(gameRepository.findByName("checkers")).thenReturn(Optional.of(existingGame));
        when(gameRepository.findByName("DifferentName")).thenReturn(Optional.of(otherGameWithName));

        assertThatThrownBy(() -> service.register("checkers", request))
                .isInstanceOf(DuplicateGameNameException.class);

        verify(gameRepository).findByName("checkers");
        verify(gameRepository).findByName("DifferentName");
        verifyNoInteractions(gameHealthChecker);
        verify(gameRepository, never()).save(any());
    }

    @Test
    @DisplayName("register → unhealthy game → throws GameUnhealthyException")
    void register_unhealthyGame() {
        Game existingGame = sampleGame(GameId.from(UUID.randomUUID()));

        RegisterGameRequest request = new RegisterGameRequest(
                "UpdatedCheckers",
                "http://localhost:8088",
                "http://localhost:3001",
                "/start",
                "/health",
                "Updated Checkers",
                "An updated game",
                "http://newImg",
                BigDecimal.valueOf(30),
                GameCategory.STRATEGY,
                List.of()
        );

        when(gameRepository.findByName("checkers")).thenReturn(Optional.of(existingGame));
        when(gameRepository.findByName("UpdatedCheckers")).thenReturn(Optional.empty());
        when(gameHealthChecker.isHealthy(request)).thenReturn(false);

        assertThatThrownBy(() -> service.register("checkers", request))
                .isInstanceOf(GameUnhealthyException.class);

        verify(gameRepository).findByName("checkers");
        verify(gameRepository).findByName("UpdatedCheckers");
        verify(gameHealthChecker).isHealthy(request);
        verify(gameRepository, never()).save(any());
    }

    @Test
    @DisplayName("register → same name as current → allows update")
    void register_sameNameAsCurrentGame() {
        Game existingGame = sampleGame(GameId.from(UUID.randomUUID()));

        RegisterGameRequest request = new RegisterGameRequest(
                "checkers",
                "http://localhost:8088",
                "http://localhost:3001",
                "/newStart",
                "/newHealth",
                "Updated Checkers",
                "An updated game",
                "http://newImg",
                BigDecimal.valueOf(30),
                GameCategory.STRATEGY,
                List.of()
        );

        when(gameRepository.findByName("checkers")).thenReturn(Optional.of(existingGame));
        when(gameHealthChecker.isHealthy(request)).thenReturn(true);

        Game result = service.register("checkers", request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("checkers");

        verify(gameRepository).findByName("checkers");
        verify(gameHealthChecker).isHealthy(request);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("findByName → returns entity when present")
    void findByName_found() {
        Game game = sampleGame(GameId.from(UUID.randomUUID()));

        when(gameRepository.findByName("checkers")).thenReturn(Optional.of(game));

        Game result = service.findByName("checkers");

        assertThat(result).isEqualTo(game);
        verify(gameRepository).findByName("checkers");
    }

    @Test
    @DisplayName("findByName → throws when missing")
    void findByName_notFound() {
        when(gameRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByName("nonexistent"))
                .isInstanceOf(GameNotFoundException.class);

        verify(gameRepository).findByName("nonexistent");
    }

    @Test
    @DisplayName("getAchievements → returns list of achievements")
    void getAchievements_returnsAchievements() {
        GameId gameId = GameId.from(UUID.randomUUID());
        Game game = sampleGame(gameId);
        Achievement achievement1 = new Achievement(new AchievementKey("key1"), "Achievement 1", "Description 1", 5);
        Achievement achievement2 = new Achievement(new AchievementKey("key2"), "Achievement 2", "Description 2", 3);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        List<Achievement> results = service.getAchievements(gameId);

        assertThat(results).isNotNull();
        verify(gameRepository).findById(gameId);
    }

    @Test
    @DisplayName("getAchievements → game not found → throws GameNotFoundException")
    void getAchievements_gameNotFound() {
        GameId gameId = GameId.from(UUID.randomUUID());

        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAchievements(gameId))
                .isInstanceOf(GameNotFoundException.class);

        verify(gameRepository).findById(gameId);
    }

    @Test
    @DisplayName("getAchievement → returns achievement when present")
    void getAchievement_found() {
        GameId gameId = GameId.from(UUID.randomUUID());
        AchievementKey key = new AchievementKey("key1");
        Achievement achievement = new Achievement(key, "Achievement 1", "Description 1", 5);

        when(gameRepository.findAchievementById(key, gameId)).thenReturn(Optional.of(achievement));

        Achievement result = service.getAchievement(gameId, key);

        assertThat(result).isEqualTo(achievement);
        assertThat(result.name()).isEqualTo("Achievement 1");
        verify(gameRepository).findAchievementById(key, gameId);
    }

    @Test
    @DisplayName("getAchievement → achievement not found → throws AchievementNotFoundException")
    void getAchievement_notFound() {
        GameId gameId = GameId.from(UUID.randomUUID());
        AchievementKey key = new AchievementKey("nonexistent");

        when(gameRepository.findAchievementById(key, gameId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAchievement(gameId, key))
                .isInstanceOf(AchievementNotFoundException.class);

        verify(gameRepository).findAchievementById(key, gameId);
    }
}