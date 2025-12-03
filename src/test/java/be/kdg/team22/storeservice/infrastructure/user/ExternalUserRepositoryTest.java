package be.kdg.team22.storeservice.infrastructure.user;

import be.kdg.team22.storeservice.domain.exceptions.ServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExternalUserRepositoryTest {

    final UUID GAME = UUID.randomUUID();
    final String JWT = "token";
    RestClient client;
    RestClient.RequestHeadersSpec<?> headerSpec;
    ExternalUserRepository repo;

    @BeforeEach
    void setup() {

        client = mock(RestClient.class, RETURNS_DEEP_STUBS);

        headerSpec = mock(RestClient.RequestHeadersSpec.class, RETURNS_DEEP_STUBS);

        Mockito.<RestClient.RequestHeadersSpec<?>>when(
                client.get()
                        .uri("/me")
                        .header(eq("Authorization"), any(String[].class))
        ).thenReturn(headerSpec);

        repo = new ExternalUserRepository(client);
    }

    @Test
    @DisplayName("userOwnsGame → true when game in library")
    void owns_true() {

        List<LibraryGameResponse> library =
                List.of(new LibraryGameResponse(GAME, "title", false));

        when(headerSpec.retrieve()
                .body(any(ParameterizedTypeReference.class)))
                .thenReturn(library);

        assertThat(repo.userOwnsGame(GAME, JWT)).isTrue();
    }

    @Test
    @DisplayName("userOwnsGame → false when game not in library")
    void owns_false() {

        List<LibraryGameResponse> library =
                List.of(new LibraryGameResponse(UUID.randomUUID(), "title", false));

        when(headerSpec.retrieve()
                .body(any(ParameterizedTypeReference.class)))
                .thenReturn(library);

        assertThat(repo.userOwnsGame(GAME, JWT)).isFalse();
    }

    @Test
    @DisplayName("userOwnsGame → false when list null")
    void owns_nullList() {

        when(headerSpec.retrieve()
                .body(any(ParameterizedTypeReference.class)))
                .thenReturn(null);

        assertThat(repo.userOwnsGame(GAME, JWT)).isFalse();
    }

    @Test
    @DisplayName("userOwnsGame → false when list empty")
    void owns_empty() {

        when(headerSpec.retrieve()
                .body(any(ParameterizedTypeReference.class)))
                .thenReturn(List.of());

        assertThat(repo.userOwnsGame(GAME, JWT)).isFalse();
    }

    @Test
    @DisplayName("userOwnsGame → RestClientException → UserServiceUnavailable")
    void owns_restError() {

        when(headerSpec.retrieve()
                .body(any(ParameterizedTypeReference.class)))
                .thenThrow(new RestClientException("boom"));

        assertThatThrownBy(() -> repo.userOwnsGame(GAME, JWT))
                .isInstanceOf(ServiceUnavailableException.class);
    }
}