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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExternalGamesRepositoryTest {

    private final ExternalGamesRepository repo = spy(new ExternalGamesRepository());

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

    private RestClient mockClientReturning(EngineGameResponse body) {
        RestClient mockClient = mock(RestClient.class, RETURNS_DEEP_STUBS);

        when(mockClient.post()
                .uri("")
                .body(any(EngineCreateGameRequest.class))
                .retrieve()
                .body(eq(EngineGameResponse.class))
        ).thenReturn(body);

        return mockClient;
    }

    private RestClient mockClientThrowing(Exception ex) {
        RestClient mockClient = mock(RestClient.class, RETURNS_DEEP_STUBS);

        when(mockClient.post()
                .uri("")
                .body(any(EngineCreateGameRequest.class))
                .retrieve()
                .body(eq(EngineGameResponse.class))
        ).thenThrow(ex);

        return mockClient;
    }

    @Test
    @DisplayName("startExternalGame – success returns engine gameId")
    void startExternalGame_success() {

        UUID instanceId = UUID.fromString("99999999-aaaa-aaaa-aaaa-999999999999");
        EngineGameResponse response = new EngineGameResponse(instanceId);

        RestClient mock = mockClientReturning(response);
        doReturn(mock).when(repo).createRestClient(anyString());

        UUID result = repo.startExternalGame(
                sampleGame(),
                sampleRequest(new TicTacToeSettingsModel(3))
        );

        assertThat(result).isEqualTo(instanceId);
    }

    @Test
    @DisplayName("startExternalGame – 404 triggers EngineGameNotFoundException")
    void startExternalGame_notFound() {
        RestClient mock = mockClientThrowing(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        doReturn(mock).when(repo).createRestClient(anyString());

        assertThatThrownBy(() ->
                repo.startExternalGame(sampleGame(), sampleRequest(new CheckersSettingsModel(8, true)))
        ).isInstanceOf(EngineGameNotFoundException.class);
    }

    @Test
    @DisplayName("startExternalGame – other 4xx rethrows HttpClientErrorException")
    void startExternalGame_other4xx() {
        RestClient mock = mockClientThrowing(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        doReturn(mock).when(repo).createRestClient(anyString());

        assertThatThrownBy(() ->
                repo.startExternalGame(sampleGame(), sampleRequest(new TicTacToeSettingsModel(3)))
        ).isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    @DisplayName("startExternalGame – RestClientException triggers EngineNotReachableException")
    void startExternalGame_notReachable() {
        RestClient mock = mockClientThrowing(new RestClientException("connection refused"));

        doReturn(mock).when(repo).createRestClient(anyString());

        assertThatThrownBy(() ->
                repo.startExternalGame(sampleGame(), sampleRequest(new CheckersSettingsModel(8, false)))
        )
                .isInstanceOf(EngineNotReachableException.class)
                .hasMessageContaining("http://engine-service/start");
    }
}