package be.kdg.team22.gamesservice.application.game;

import be.kdg.team22.gamesservice.api.game.models.CheckersSettingsModel;
import be.kdg.team22.gamesservice.api.game.models.GameSettingsModel;
import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameResponseModel;
import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import be.kdg.team22.gamesservice.domain.game.GameRepository;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameNotFoundException;
import be.kdg.team22.gamesservice.domain.game.exceptions.InvalidGameConfigurationException;
import be.kdg.team22.gamesservice.domain.game.exceptions.PlayersListEmptyException;
import be.kdg.team22.gamesservice.infrastructure.game.engine.EngineStartRequest;
import be.kdg.team22.gamesservice.infrastructure.game.engine.ExternalGamesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private final GameRepository gameRepository = mock(GameRepository.class);
    private final ExternalGamesRepository engine = mock(ExternalGamesRepository.class);
    private final GameService service = new GameService(gameRepository, engine);

    private Game sampleGame(GameId id) {
        return new Game(id, "checkers", "http://localhost:8087", "/start");
    }

    private StartGameRequest request(GameSettingsModel settings) {
        return new StartGameRequest(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                UUID.fromString("00000000-0000-0000-0000-000000000002"),
                List.of(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        UUID.fromString("22222222-2222-2222-2222-222222222222")
                ),
                settings
        );
    }

    @Test
    @DisplayName("startGame – happy path calls engine and returns instanceId")
    void startGame_happyPath() {
        GameId gameId = GameId.from(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        GameSettingsModel settings = new CheckersSettingsModel(8, true);
        StartGameRequest request = request(settings);

        Game game = sampleGame(gameId);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        UUID engineInstanceId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        when(engine.startExternalGame(eq(game), any(EngineStartRequest.class))).thenReturn(engineInstanceId);

        StartGameResponseModel response = service.startGame(request);

        assertThat(response.gameInstanceId()).isEqualTo(engineInstanceId);

        ArgumentCaptor<EngineStartRequest> captor = ArgumentCaptor.forClass(EngineStartRequest.class);
        verify(engine).startExternalGame(eq(game), captor.capture());

        EngineStartRequest engineReq = captor.getValue();
        assertThat(engineReq.templateId()).isEqualTo(gameId.value());
        assertThat(engineReq.lobbyId()).isEqualTo(request.lobbyId());
        assertThat(engineReq.players()).containsExactlyElementsOf(request.players());
        assertThat(engineReq.settings()).isEqualTo(settings);
    }

    @Test
    @DisplayName("startGame – empty players throws PlayersListEmptyException")
    void startGame_emptyPlayersThrows() {
        StartGameRequest request = new StartGameRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(),
                new CheckersSettingsModel(8, true)
        );

        assertThatThrownBy(() -> service.startGame(request))
                .isInstanceOf(PlayersListEmptyException.class);

        verifyNoInteractions(gameRepository, engine);
    }

    @Test
    @DisplayName("startGame – null settings throws InvalidGameConfigurationException")
    void startGame_nullSettingsThrows() {
        StartGameRequest request = new StartGameRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(UUID.randomUUID()),
                null
        );

        assertThatThrownBy(() -> service.startGame(request))
                .isInstanceOf(InvalidGameConfigurationException.class)
                .hasMessageContaining("Game settings cannot be null");

        verifyNoInteractions(gameRepository, engine);
    }

    @Test
    @DisplayName("startGame – unknown gameId throws GameNotFoundException")
    void startGame_gameNotFoundThrows() {
        GameId id = GameId.from(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        StartGameRequest request = request(new CheckersSettingsModel(8, true));

        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.startGame(request))
                .isInstanceOf(GameNotFoundException.class);

        verify(gameRepository).findById(id);
        verifyNoInteractions(engine);
    }
}