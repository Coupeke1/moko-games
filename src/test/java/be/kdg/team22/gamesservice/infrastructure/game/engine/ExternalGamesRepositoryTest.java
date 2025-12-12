package be.kdg.team22.gamesservice.infrastructure.game.engine;

import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
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
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ExternalGamesRepositoryTest {

    private final ExternalGamesRepository repo = spy(new ExternalGamesRepository());

    private Game sampleGame() {
        return new Game(
                GameId.from(UUID.fromString("00000000-0000-0000-0000-000000000111")),
                "tic-tac-toe",
                "http://engine-service",
                "/start",
                "/start",
                "/health",
                "Tic Tac Toe",
                "desc",
                "img");
    }

    private StartGameRequest req(GameSettingsModel settings) {
        return new StartGameRequest(
                UUID.randomUUID(),
                UUID.fromString("00000000-0000-0000-0000-000000000333"),
                List.of(UUID.randomUUID(), UUID.randomUUID()),
                settings,
                true
        );
    }

    private RestClient mockSuccess(EngineGameResponse body) {
        RestClient mock = mock(RestClient.class, RETURNS_DEEP_STUBS);

        when(mock.post()
                .uri(any(Function.class))
                .body(any(EngineCreateGameRequest.class))
                .retrieve()
                .body(EngineGameResponse.class)
        ).thenReturn(body);

        return mock;
    }

    private RestClient mockFailure(Exception ex) {
        RestClient mock = mock(RestClient.class, RETURNS_DEEP_STUBS);

        when(mock.post()
                .uri(any(Function.class))
                .body(any(EngineCreateGameRequest.class))
                .retrieve()
                .body(EngineGameResponse.class)
        ).thenThrow(ex);

        return mock;
    }

    @Test
    @DisplayName("startExternalGame – success returns engine gameId")
    void success() {
        UUID instanceId = UUID.fromString("99999999-aaaa-aaaa-aaaa-999999999999");
        RestClient client = mockSuccess(new EngineGameResponse(instanceId));

        doReturn(client).when(repo).createRestClient(anyString());

        UUID result = repo.startExternalGame(
                sampleGame(),
                req(new TicTacToeSettingsModel(3))
        );

        assertThat(result).isEqualTo(instanceId);
    }

    @Test
    @DisplayName("startExternalGame – 404 triggers EngineGameNotFoundException")
    void notFound() {
        RestClient client = mockFailure(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        doReturn(client).when(repo).createRestClient(anyString());

        assertThatThrownBy(() ->
                repo.startExternalGame(sampleGame(), req(new CheckersSettingsModel(8, true)))
        ).isInstanceOf(EngineGameNotFoundException.class);
    }

    @Test
    @DisplayName("startExternalGame – other 4xx → rethrow HttpClientErrorException")
    void other4xx() {
        RestClient client = mockFailure(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        doReturn(client).when(repo).createRestClient(anyString());

        assertThatThrownBy(() ->
                repo.startExternalGame(sampleGame(), req(new TicTacToeSettingsModel(3)))
        ).isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    @DisplayName("startExternalGame – RestClientException → EngineNotReachableException")
    void unreachable() {
        RestClient client = mockFailure(new RestClientException("connection"));

        doReturn(client).when(repo).createRestClient(anyString());

        assertThatThrownBy(() ->
                repo.startExternalGame(sampleGame(), req(new CheckersSettingsModel(8, false)))
        ).isInstanceOf(EngineNotReachableException.class)
                .hasMessageContaining("http://engine-service/start");
    }
}