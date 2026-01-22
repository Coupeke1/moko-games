package be.kdg.team22.sessionservice.infrastructure.player;

import be.kdg.team22.sessionservice.domain.player.exceptions.NotReachableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ExternalPlayersRepositoryTest {

    private ExternalPlayersRepository repo;

    @Test
    @DisplayName("createBot success → returns Optional")
    void createBot_success() {
        BotProfileResponse response = new BotProfileResponse(
                UUID.randomUUID(), "BOT-MOKO", "img");

        RestClient client = mock(RestClient.class, RETURNS_DEEP_STUBS);

        when(client.post()
                .uri("/bot")
                .retrieve()
                .body(eq(BotProfileResponse.class))
        ).thenReturn(response);

        repo = new ExternalPlayersRepository(client);

        Optional<BotProfileResponse> result = repo.createBot();

        assertThat(result).isPresent();
        assertThat(result.get().username()).isEqualTo("BOT-MOKO");
    }

    @Test
    @DisplayName("createBot 400 → rethrow HttpClientErrorException")
    void createBot_badRequest() {

        RestClient client = mock(RestClient.class, RETURNS_DEEP_STUBS);

        when(client.post()
                .uri("/bot")
                .retrieve()
                .body(eq(BotProfileResponse.class))
        ).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        repo = new ExternalPlayersRepository(client);

        assertThatThrownBy(() -> repo.createBot())
                .isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    @DisplayName("createBot RestClientException → NotReachableException")
    void createBot_notReachable() {

        RestClient client = mock(RestClient.class, RETURNS_DEEP_STUBS);

        when(client.post()
                .uri("/bot")
                .retrieve()
                .body(eq(BotProfileResponse.class))
        ).thenThrow(new RestClientException("connection refused"));

        repo = new ExternalPlayersRepository(client);

        assertThatThrownBy(() -> repo.createBot())
                .isInstanceOf(NotReachableException.class);
    }
}