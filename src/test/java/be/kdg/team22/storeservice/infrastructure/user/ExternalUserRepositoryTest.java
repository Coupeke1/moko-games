package be.kdg.team22.storeservice.infrastructure.user;

import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.domain.exceptions.ServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExternalUserRepositoryTest {
    final UUID GAME = UUID.randomUUID();
    final String JWT = "token";
    RestClient client;
    RestClient.RequestHeadersSpec<?> headerSpec;
    ExternalUserRepository repository;

    @BeforeEach
    void setup() {
        client = mock(RestClient.class, RETURNS_DEEP_STUBS);
        headerSpec = mock(RestClient.RequestHeadersSpec.class, RETURNS_DEEP_STUBS);

        Mockito.<RestClient.RequestHeadersSpec<?>>when(client.get().uri("/me").header(eq("Authorization"), any(String[].class))).thenReturn(headerSpec);

        repository = new ExternalUserRepository(client);
    }

    @Test
    @DisplayName("userOwnsGame → true when backend returns true")
    void owns_true() {
        when(headerSpec.retrieve().body(Boolean.class)).thenReturn(true);
        assertThat(repository.userOwnsGame(GameId.from(GAME), JWT)).isTrue();
    }


    @Test
    @DisplayName("userOwnsGame → false when backend returns false")
    void owns_false() {
        when(headerSpec.retrieve().body(Boolean.class)).thenReturn(false);
        assertThat(repository.userOwnsGame(GameId.from(GAME), JWT)).isFalse();
    }


    @Test
    @DisplayName("userOwnsGame → RestClientException → UserServiceUnavailable")
    void owns_restError() {
        when(headerSpec.retrieve().body(Boolean.class)).thenThrow(new RestClientException("boom"));

        assertThatThrownBy(() -> repository.userOwnsGame(GameId.from(GAME), JWT)).isInstanceOf(ServiceUnavailableException.class);
    }


    @Test
    @DisplayName("userOwnsGame → false when backend returns false")
    void owns_empty() {
        when(headerSpec.retrieve().body(Boolean.class)).thenReturn(false);
        assertThat(repository.userOwnsGame(GameId.from(GAME), JWT)).isFalse();
    }

    @Test
    @DisplayName("userOwnsGame → false when backend returns null")
    void owns_null() {
        when(headerSpec.retrieve().body(Boolean.class)).thenReturn(null);
        assertThat(repository.userOwnsGame(GameId.from(GAME), JWT)).isFalse();
    }
}