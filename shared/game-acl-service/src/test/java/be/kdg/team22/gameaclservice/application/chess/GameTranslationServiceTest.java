package be.kdg.team22.gameaclservice.application.chess;

import be.kdg.team22.gameaclservice.api.model.CreateChessGameModel;
import be.kdg.team22.gameaclservice.config.ChessInfoProperties;
import be.kdg.team22.gameaclservice.domain.Game;
import be.kdg.team22.gameaclservice.events.inbound.ChessAchievementEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessGameEndedEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessMessageType;
import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;
import be.kdg.team22.gameaclservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.gameaclservice.infrastructure.games.RegisterGameRequest;
import be.kdg.team22.gameaclservice.infrastructure.messaging.AchievementEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameTranslationService")
class GameTranslationServiceTest {
    @Mock
    private AchievementEventPublisher achievementEventPublisher;
    @Mock
    private ExternalGamesRepository gameRepository;
    @Mock
    private ChessInfoProperties chessInfoProperties;

    @InjectMocks
    private GameTranslationService gameTranslationService;

    @Test
    @DisplayName("should register game with converted event data")
    void testTranslateAndRegisterGame() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://localhost:3000",
                "http://image.url/chess.png",
                List.of(
                        new ChessRegisterEvent.ChessAchievement("first_move", "Make your first move"),
                        new ChessRegisterEvent.ChessAchievement("checkmate", "Achieve checkmate")
                ),
                ChessMessageType.GAME_REGISTERED,
                LocalDateTime.now()
        );

        when(chessInfoProperties.price()).thenReturn(BigDecimal.valueOf(9.99));
        when(chessInfoProperties.category()).thenReturn("Board Games");
        String aclBackendUrl = "http://acl-backend:8080";
        ReflectionTestUtils.setField(gameTranslationService, "aclBackendUrl", aclBackendUrl);

        gameTranslationService.translateAndRegisterGame(event);

        ArgumentCaptor<RegisterGameRequest> captor = ArgumentCaptor.forClass(RegisterGameRequest.class);
        verify(gameRepository).registerGame(captor.capture());

        RegisterGameRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.name()).isEqualTo("chess");
        assertThat(capturedRequest.backendUrl()).isEqualTo(aclBackendUrl);
        assertThat(capturedRequest.frontendUrl()).isEqualTo("http://localhost:3000");
        assertThat(capturedRequest.title()).isEqualTo("Chess");
        assertThat(capturedRequest.startEndpoint()).isEqualTo("/api/games/chess");
        assertThat(capturedRequest.healthEndpoint()).isEqualTo("/actuator/health");
        assertThat(capturedRequest.image()).isEqualTo("http://image.url/chess.png");
        assertThat(capturedRequest.category()).isEqualTo("Board Games");
        assertThat(capturedRequest.achievements()).hasSize(2);
    }

    @Test
    @DisplayName("should handle empty achievements list during registration")
    void testTranslateAndRegisterGameWithNoAchievements() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend.url",
                "http://image.url/chess.png",
                List.of(),
                ChessMessageType.GAME_REGISTERED,
                LocalDateTime.now()
        );

        when(chessInfoProperties.price()).thenReturn(BigDecimal.ONE);
        when(chessInfoProperties.category()).thenReturn("Games");
        ReflectionTestUtils.setField(gameTranslationService, "aclBackendUrl", "http://backend");

        gameTranslationService.translateAndRegisterGame(event);

        ArgumentCaptor<RegisterGameRequest> captor = ArgumentCaptor.forClass(RegisterGameRequest.class);
        verify(gameRepository).registerGame(captor.capture());

        assertThat(captor.getValue().achievements()).isEmpty();
    }

    @Test
    @DisplayName("should publish achievement event with converted data")
    void testTranslateAndSendAchievement() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        ChessAchievementEvent event = new ChessAchievementEvent(
                gameId,
                playerId,
                "John",
                "first_move",
                "Make your first move",
                ChessMessageType.ACHIEVEMENT_ACQUIRED,
                timestamp
        );

        gameTranslationService.translateAndSendAchievement(event);

        ArgumentCaptor<be.kdg.team22.gameaclservice.events.outbound.GameAchievementEvent> captor =
                ArgumentCaptor.forClass(be.kdg.team22.gameaclservice.events.outbound.GameAchievementEvent.class);

        verify(achievementEventPublisher).publishAchievementEvent(captor.capture());

        var capturedEvent = captor.getValue();
        assertThat(capturedEvent.achievementCode()).isEqualTo("first_move");
        assertThat(capturedEvent.gameName()).isEqualTo("chess");
        assertThat(capturedEvent.gameId()).isEqualTo(gameId);
        assertThat(capturedEvent.playerId()).isEqualTo(playerId);
        assertThat(capturedEvent.occurredAt()).isNotNull();
    }

    @Test
    @DisplayName("should publish game ended event with game instance id")
    void testTranslateAndSendGameEnded() {
        UUID gameId = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        ChessGameEndedEvent event = new ChessGameEndedEvent(
                gameId,
                "white_player",
                "black_player",
                "8/8/8/8/8/8/8/8",
                "checkmate",
                "white_player",
                42,
                ChessMessageType.GAME_ENDED,
                timestamp
        );

        gameTranslationService.translateAndSendGameEnded(event);

        ArgumentCaptor<be.kdg.team22.gameaclservice.events.outbound.GameEndedEvent> captor =
                ArgumentCaptor.forClass(be.kdg.team22.gameaclservice.events.outbound.GameEndedEvent.class);

        verify(achievementEventPublisher).publishGameEndedEvent(captor.capture());

        var capturedEvent = captor.getValue();
        assertThat(capturedEvent.instanceId()).isEqualTo(gameId);
        assertThat(capturedEvent.occurredAt()).isNotNull();
    }

    @Test
    @DisplayName("should create game with provided players")
    void testStartChessGame() {
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();
        List<UUID> players = List.of(player1, player2);

        CreateChessGameModel request = new CreateChessGameModel(players);

        Game game = gameTranslationService.startChessGame(request);

        assertThat(game).isNotNull();
        assertThat(game.id()).isNotNull();
        assertThat(game.players()).containsExactlyElementsOf(players);
    }

    @Test
    @DisplayName("should create game with unique id on each call")
    void testStartChessGameCreatesUniqueIds() {
        List<UUID> players = List.of(UUID.randomUUID(), UUID.randomUUID());
        CreateChessGameModel request = new CreateChessGameModel(players);

        Game game1 = gameTranslationService.startChessGame(request);
        Game game2 = gameTranslationService.startChessGame(request);

        assertThat(game1.id()).isNotEqualTo(game2.id());
    }

    @Test
    @DisplayName("should preserve all achievement details during conversion")
    void testAchievementConversionPreservesDetails() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        String achievementType = "victory_in_10_moves";
        LocalDateTime eventTime = LocalDateTime.now();

        ChessAchievementEvent event = new ChessAchievementEvent(
                gameId,
                playerId,
                "Champion",
                achievementType,
                "Win a game in 10 moves or less",
                ChessMessageType.ACHIEVEMENT_ACQUIRED,
                eventTime
        );

        gameTranslationService.translateAndSendAchievement(event);

        ArgumentCaptor<be.kdg.team22.gameaclservice.events.outbound.GameAchievementEvent> captor =
                ArgumentCaptor.forClass(be.kdg.team22.gameaclservice.events.outbound.GameAchievementEvent.class);

        verify(achievementEventPublisher).publishAchievementEvent(captor.capture());

        var result = captor.getValue();
        assertThat(result)
                .extracting("achievementCode", "gameId", "playerId")
                .containsExactly(achievementType, gameId, playerId);
    }

    @Test
    @DisplayName("should include description in game registration")
    void testGameRegistrationIncludesDescription() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://localhost:3000",
                "http://image.url/chess.png",
                List.of(),
                ChessMessageType.GAME_REGISTERED,
                LocalDateTime.now()
        );

        when(chessInfoProperties.price()).thenReturn(BigDecimal.valueOf(5.00));
        when(chessInfoProperties.category()).thenReturn("Strategy");
        ReflectionTestUtils.setField(gameTranslationService, "aclBackendUrl", "http://backend");

        gameTranslationService.translateAndRegisterGame(event);

        ArgumentCaptor<RegisterGameRequest> captor = ArgumentCaptor.forClass(RegisterGameRequest.class);
        verify(gameRepository).registerGame(captor.capture());

        assertThat(captor.getValue().description()).isEqualTo("Play chess against other players online.");
    }

    @Test
    @DisplayName("should create game preserving player order")
    void testStartChessGamePreservesPlayerOrder() {
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();
        UUID player3 = UUID.randomUUID();
        List<UUID> players = List.of(player1, player2, player3);

        CreateChessGameModel request = new CreateChessGameModel(players);
        Game game = gameTranslationService.startChessGame(request);

        assertThat(game.players())
                .containsExactly(player1, player2, player3);
    }
}
