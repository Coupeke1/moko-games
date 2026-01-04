package be.kdg.team22.gamesservice.application.game;

import be.kdg.team22.gamesservice.api.game.models.RegisterGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameResponseModel;
import be.kdg.team22.gamesservice.domain.game.*;
import be.kdg.team22.gamesservice.domain.game.exceptions.*;
import be.kdg.team22.gamesservice.domain.game.settings.GameSettingsDefinition;
import be.kdg.team22.gamesservice.domain.game.settings.SettingDefinition;
import be.kdg.team22.gamesservice.domain.game.settings.SettingType;
import be.kdg.team22.gamesservice.infrastructure.game.engine.ExternalGamesRepository;
import be.kdg.team22.gamesservice.infrastructure.game.health.GameHealthChecker;
import be.kdg.team22.gamesservice.infrastructure.store.ExternalStoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private final GameRepository gameRepository = mock(GameRepository.class);
    private final GameHealthChecker gameHealthChecker = mock(GameHealthChecker.class);
    private final ExternalStoreRepository externalStoreRepository = mock(ExternalStoreRepository.class);
    private final ExternalGamesRepository engine = mock(ExternalGamesRepository.class);

    private final GameService service = new GameService(
            gameRepository, gameHealthChecker, externalStoreRepository, engine
    );

    private GameSettingsDefinition checkersSettingsDefinition() {
        return new GameSettingsDefinition(List.of(
                new SettingDefinition(
                        "boardSize",
                        SettingType.INTEGER,
                        true,
                        3,
                        20,
                        null,
                        8
                ),
                new SettingDefinition(
                        "flyingKings",
                        SettingType.BOOLEAN,
                        false,
                        null,
                        null,
                        null,
                        false
                )
        ));
    }

    private Game sampleGame(GameId id) {
        Instant now = Instant.parse("2024-01-01T10:00:00Z");
        return new Game(
                id,
                "checkers",
                "http://localhost:8087",
                "http://localhost:3000",
                "/start",
                "/health",
                null,
                true,
                "Checkers",
                "A fun board game",
                "http://img",
                now,
                now,
                checkersSettingsDefinition()
        );
    }

    private Game faultySampleGame(GameId id) {
        Instant now = Instant.parse("2024-01-01T10:00:00Z");
        return new Game(
                id,
                "checkers",
                "http://localhost:8087",
                "http://localhost:3000",
                "/start",
                "/health",
                null,
                true,
                "Checkers",
                "A fun board game",
                "http://img",
                now,
                now,
                null);
    }

    private StartGameRequest createRequest(Map<String, Object> settings, boolean ai) {
        return new StartGameRequest(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                List.of(
                        UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                        UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
                ),
                ai,
                settings
        );
    }

    private Jwt tokenFromUuid(UUID id) {
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

        StartGameRequest request = createRequest(
                Map.of("boardSize", 8, "flyingKings", true),
                true
        );

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        UUID instanceId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        Jwt token = tokenFromUuid(request.players().getFirst());

        when(engine.startExternalGame(eq(game), eq(request), eq(token)))
                .thenReturn(instanceId);

        StartGameResponseModel response = service.startGame(request, token);

        assertThat(response.gameInstanceId()).isEqualTo(instanceId);

        verify(gameRepository).findById(gameId);
        verify(engine).startExternalGame(game, request, token);
    }

    @Test
    @DisplayName("startGame → empty players → PlayersListEmptyException")
    void startGame_emptyPlayers() {
        StartGameRequest request = new StartGameRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(),
                false,
                Map.of("boardSize", 8)
        );

        assertThatThrownBy(() -> service.startGame(request, null))
                .isInstanceOf(PlayersListEmptyException.class);

        verifyNoInteractions(gameRepository, engine);
    }

    @Test
    @DisplayName("startGame → null settings → InvalidGameSettingsException")
    void startGame_nullSettings() {
        StartGameRequest request = new StartGameRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(UUID.randomUUID()),
                false,
                null
        );

        assertThatThrownBy(() -> service.startGame(request, tokenFromUuid(request.players().getFirst())))
                .isInstanceOf(InvalidGameSettingsException.class);

        verifyNoInteractions(gameRepository, engine);
    }

    @Test
    @DisplayName("startGame → invalid settings (missing required) → InvalidGameSettingsException")
    void startGame_missingRequiredSetting() {
        GameId gameId = GameId.from(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        Game game = faultySampleGame(gameId);

        StartGameRequest request = createRequest(Map.of("flyingKings", true), false);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        assertThatThrownBy(() -> service.startGame(request, tokenFromUuid(request.players().getFirst())))
                .isInstanceOf(InvalidGameSettingsException.class);

        verify(gameRepository).findById(gameId);
        verifyNoInteractions(engine);
    }

    @Test
    @DisplayName("startGame → game not found → GameNotFoundException")
    void startGame_gameNotFound() {
        GameId id = GameId.from(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        StartGameRequest request = createRequest(Map.of("boardSize", 8), false);

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
    @DisplayName("create/register → happy path → creates and saves game")
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
                checkersSettingsDefinition(),
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
    @DisplayName("create/register → duplicate game name → throws DuplicateGameNameException")
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
                checkersSettingsDefinition(),
                List.of()
        );

        Game existingGame = sampleGame(GameId.from(UUID.randomUUID()));

        when(gameRepository.findByName("Checkers")).thenReturn(Optional.of(existingGame));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateGameNameException.class);

        verify(gameRepository).findByName("Checkers");
        verifyNoInteractions(gameHealthChecker);
        verify(gameRepository, never()).save(any());
    }

    @Test
    @DisplayName("create/register → unhealthy game → throws GameUnhealthyException")
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
                checkersSettingsDefinition(),
                List.of()
        );

        when(gameRepository.findByName("Checkers")).thenReturn(Optional.empty());
        when(gameHealthChecker.isHealthy(request)).thenReturn(false);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(GameUnhealthyException.class);

        verify(gameRepository).findByName("Checkers");
        verify(gameHealthChecker, times(3)).isHealthy(request);
        verify(gameRepository, never()).save(any());
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
    @DisplayName("getAchievements → game not found → throws GameNotFoundException")
    void getAchievements_gameNotFound() {
        GameId gameId = GameId.from(UUID.randomUUID());

        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAchievements(gameId))
                .isInstanceOf(GameNotFoundException.class);

        verify(gameRepository).findById(gameId);
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