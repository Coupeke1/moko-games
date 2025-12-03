package be.kdg.team22.storeservice.infrastructure.games;

import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.domain.exceptions.ServiceUnavailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ExternalGamesRepositoryTest {

    @Test
    @DisplayName("fetchMetadata → returns metadata")
    void fetch_ok() {
        UUID gameId = UUID.randomUUID();
        GameMetadataResponse resp = new GameMetadataResponse(
                gameId, "name", "title", "desc", "img",
                Instant.now(), Instant.now()
        );

        RestClient client = mock(RestClient.class, RETURNS_DEEP_STUBS);

        when(client.get()
                .uri("/" + gameId)
                .retrieve()
                .body(eq(GameMetadataResponse.class))
        ).thenReturn(resp);

        ExternalGamesRepository repo = new ExternalGamesRepository(client);

        GameMetadataResponse result = repo.fetchMetadata(gameId);

        assertThat(result.id()).isEqualTo(gameId);
    }

    @Test
    @DisplayName("fetchMetadata → null body = GameNotFound")
    void fetch_nullBody() {
        UUID gameId = UUID.randomUUID();

        RestClient client = mock(RestClient.class, RETURNS_DEEP_STUBS);

        when(client.get()
                .uri("/" + gameId)
                .retrieve()
                .body(eq(GameMetadataResponse.class))
        ).thenReturn(null);

        ExternalGamesRepository repo = new ExternalGamesRepository(client);

        assertThatThrownBy(() -> repo.fetchMetadata(gameId))
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    @DisplayName("fetchMetadata → 404 = GameNotFound")
    void fetch_404() {
        UUID gameId = UUID.randomUUID();

        RestClient client = mock(RestClient.class, RETURNS_DEEP_STUBS);

        when(client.get()
                .uri("/" + gameId)
                .retrieve()
                .body(eq(GameMetadataResponse.class))
        ).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        ExternalGamesRepository repo = new ExternalGamesRepository(client);

        assertThatThrownBy(() -> repo.fetchMetadata(gameId))
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    @DisplayName("fetchMetadata → other Http error = rethrow")
    void fetch_otherHttpError() {
        UUID gameId = UUID.randomUUID();
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        RestClient client = mock(RestClient.class, RETURNS_DEEP_STUBS);

        when(client.get()
                .uri("/" + gameId)
                .retrieve()
                .body(eq(GameMetadataResponse.class))
        ).thenThrow(ex);

        ExternalGamesRepository repo = new ExternalGamesRepository(client);

        assertThatThrownBy(() -> repo.fetchMetadata(gameId))
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining("400");
    }

    @Test
    @DisplayName("fetchMetadata → RestClientException → ServiceUnavailable")
    void fetch_restError() {
        UUID gameId = UUID.randomUUID();

        RestClient client = mock(RestClient.class, RETURNS_DEEP_STUBS);

        when(client.get()
                .uri("/" + gameId)
                .retrieve()
                .body(eq(GameMetadataResponse.class))
        ).thenThrow(new RestClientException("boom"));

        ExternalGamesRepository repo = new ExternalGamesRepository(client);

        assertThatThrownBy(() -> repo.fetchMetadata(gameId))
                .isInstanceOf(ServiceUnavailableException.class);
    }
}