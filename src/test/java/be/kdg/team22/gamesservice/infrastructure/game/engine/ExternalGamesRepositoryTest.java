package be.kdg.team22.gamesservice.infrastructure.game.engine;

import be.kdg.team22.gamesservice.api.game.models.CheckersSettingsModel;
import be.kdg.team22.gamesservice.api.game.models.GameSettingsModel;
import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.api.game.models.TicTacToeSettingsModel;
import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import be.kdg.team22.gamesservice.domain.game.exceptions.EngineGameNotFoundException;
import be.kdg.team22.gamesservice.domain.game.exceptions.EngineNotReachableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ExternalGamesRepositoryTest {

    private final ExternalGamesRepository repo = new ExternalGamesRepository();

    private Game sampleGame() {
        return new Game(
                GameId.from(UUID.fromString("00000000-0000-0000-0000-000000000111")),
                "tic-tac-toe",
                "http://engine-service",
                "/start"
        );
    }

    private StartGameRequest sampleRequest(GameSettingsModel settings) {
        return new StartGameRequest(
                UUID.fromString("00000000-0000-0000-0000-000000000222"),
                UUID.fromString("00000000-0000-0000-0000-000000000333"),
                List.of(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        UUID.fromString("22222222-2222-2222-2222-222222222222")
                ),
                settings
        );
    }

    private RestClient mockRestClientReturning(Object body) {
        RestClient mockClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        when(mockClient.post().uri("").body(any()).retrieve().body(any()))
                .thenReturn(body);
        return mockClient;
    }

    @Test
    @DisplayName("startExternalGame – success returns engine gameId")
    void startExternalGame_success() {

        UUID instanceId = UUID.fromString("99999999-aaaa-aaaa-aaaa-999999999999");
        EngineGameResponse response = new EngineGameResponse(instanceId);

        RestClient mockClient = mockRestClientReturning(response);

        var repoSpy = spy(repo);
        doReturn(mockClient).when(repoSpy).createRestClient(anyString());

        UUID result = repoSpy.startExternalGame(
                sampleGame(),
                sampleRequest(new TicTacToeSettingsModel(3))
        );

        assertThat(result).isEqualTo(instanceId);
    }

    @Test
    @DisplayName("startExternalGame – 404 triggers EngineGameNotFoundException")
    void startExternalGame_notFound() {

        RestClient mockClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        when(mockClient.post().uri("").body(any()).retrieve().body(any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        var repoSpy = spy(repo);
        doReturn(mockClient).when(repoSpy).createRestClient(anyString());

        assertThatThrownBy(() ->
                repoSpy.startExternalGame(sampleGame(), sampleRequest(new CheckersSettingsModel(8, true))))
                .isInstanceOf(EngineGameNotFoundException.class);
    }

    @Test
    @DisplayName("startExternalGame – other 4xx rethrows HttpClientErrorException")
    void startExternalGame_other4xx() {

        RestClient mockClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        when(mockClient.post().uri("").body(any()).retrieve().body(any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        var repoSpy = spy(repo);
        doReturn(mockClient).when(repoSpy).createRestClient(anyString());

        assertThatThrownBy(() ->
                repoSpy.startExternalGame(sampleGame(), sampleRequest(new TicTacToeSettingsModel(3))))
                .isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    @DisplayName("startExternalGame – RestClientException triggers EngineNotReachableException")
    void startExternalGame_notReachable() {

        RestClient mockClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        when(mockClient.post().uri("").body(any()).retrieve().body(any()))
                .thenThrow(new RestClientException("connection refused"));

        var repoSpy = spy(repo);
        doReturn(mockClient).when(repoSpy).createRestClient(anyString());

        assertThatThrownBy(() ->
                repoSpy.startExternalGame(sampleGame(), sampleRequest(new CheckersSettingsModel(8, false))))
                .isInstanceOf(EngineNotReachableException.class)
                .hasMessageContaining("http://engine-service/start");
    }
}