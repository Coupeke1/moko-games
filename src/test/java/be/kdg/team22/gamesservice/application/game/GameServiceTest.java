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
import be.kdg.team22.gamesservice.infrastructure.game.engine.ExternalGamesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private final GameRepository gameRepository = mock(GameRepository.class);
    private final ExternalGamesRepository engine = mock(ExternalGamesRepository.class);

    private final GameService service = new GameService(gameRepository, engine);

    private Game exampleGame(GameId id) {
        return new Game(id, "checkers", "http://localhost:8087", "/start");
    }

    private StartGameRequest createRequest(GameSettingsModel settings) {
        return new StartGameRequest(
                UUID.fromString("11111111-1111-1111-1111-111111111111"), // lobbyId
                UUID.fromString("22222222-2222-2222-2222-222222222222"), // gameId
                List.of(
                        UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                        UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
                ),
                settings
        );
    }

    @Test
    @DisplayName("startGame → happy path: repository + engine worden aangeroepen")
    void startGame_happyPath() {
        GameId gameId = GameId.from(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        Game game = exampleGame(gameId);

        StartGameRequest request = createRequest(new CheckersSettingsModel(8, true));

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        UUID instanceId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        when(engine.startExternalGame(game, request)).thenReturn(instanceId);

        StartGameResponseModel response = service.startGame(request);

        assertThat(response.gameInstanceId()).isEqualTo(instanceId);

        verify(gameRepository).findById(gameId);
        verify(engine).startExternalGame(game, request);
    }

    @Test
    @DisplayName("startGame → empty players → throw PlayersListEmptyException")
    void startGame_emptyPlayers() {
        StartGameRequest request = new StartGameRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(),
                new CheckersSettingsModel(8, true)
        );

        assertThatThrownBy(() -> service.startGame(request))
                .isInstanceOf(PlayersListEmptyException.class);

        verifyNoInteractions(gameRepository);
        verifyNoInteractions(engine);
    }

    @Test
    @DisplayName("startGame – null settings throws InvalidGameConfigurationException")
    void startGame_nullSettings() {

        UUID gameIdRaw = UUID.fromString("22222222-2222-2222-2222-222222222222");

        StartGameRequest request = new StartGameRequest(
                UUID.randomUUID(),
                gameIdRaw,
                List.of(UUID.randomUUID()),
                null
        );

        assertThatThrownBy(() -> service.startGame(request))
                .isInstanceOf(InvalidGameConfigurationException.class)
                .hasMessageContaining("Game settings cannot be null");

        verifyNoInteractions(gameRepository, engine);
    }

    @Test
    @DisplayName("startGame → gameId bestaat niet → throw GameNotFoundException")
    void startGame_gameNotFound() {
        GameId id = GameId.from(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        StartGameRequest request = createRequest(new CheckersSettingsModel(8, true));

        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.startGame(request))
                .isInstanceOf(GameNotFoundException.class);

        verify(gameRepository).findById(id);
        verifyNoInteractions(engine);
    }
}
