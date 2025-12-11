package be.kdg.team22.storeservice.infrastructure.user;

import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.domain.exceptions.ServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ExternalUserRepositoryTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String JWT = "token";

    private RestClient client;
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private RestClient.RequestHeadersSpec headerSpec;
    private RestClient.ResponseSpec responseSpec;
    private ExternalUserRepository repository;

    @BeforeEach
    void setup() {
        client = mock(RestClient.class);
        requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        headerSpec = mock(RestClient.RequestHeadersSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        when(client.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/" + GAME_ID)).thenReturn(headerSpec);
        when(headerSpec.header("Authorization", "Bearer " + JWT)).thenReturn(headerSpec);
        when(headerSpec.retrieve()).thenReturn(responseSpec);

        repository = new ExternalUserRepository(client);
    }

    @Test
    @DisplayName("userOwnsGame returns true when backend returns true")
    void userOwnsGame_backendReturnsTrue_returnsTrue() {
        when(responseSpec.body(Boolean.class)).thenReturn(true);

        boolean result = repository.userOwnsGame(GameId.from(GAME_ID), Jwt.withTokenValue(JWT).build());

        assertThat(result).isTrue();
        verify(client.get()).uri("/" + GAME_ID);
        verify(headerSpec).header("Authorization", "Bearer " + JWT);
    }

    @Test
    @DisplayName("userOwnsGame returns false when backend returns false")
    void userOwnsGame_backendReturnsFalse_returnsFalse() {
        when(responseSpec.body(Boolean.class)).thenReturn(false);

        boolean result = repository.userOwnsGame(GameId.from(GAME_ID), Jwt.withTokenValue(JWT).build());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("userOwnsGame returns false when backend returns null")
    void userOwnsGame_backendReturnsNull_returnsFalse() {
        when(responseSpec.body(Boolean.class)).thenReturn(null);

        boolean result = repository.userOwnsGame(GameId.from(GAME_ID), Jwt.withTokenValue(JWT).build());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("userOwnsGame throws ServiceUnavailableException when RestClient throws exception")
    void userOwnsGame_restClientThrowsException_throwsServiceUnavailable() {
        when(responseSpec.body(Boolean.class)).thenThrow(new RestClientException("Service unavailable"));

        assertThatThrownBy(() -> repository.userOwnsGame(GameId.from(GAME_ID), Jwt.withTokenValue(JWT).build())).isInstanceOf(ServiceUnavailableException.class).hasMessageContaining("User-Service is currently unavailable");
    }

    @Test
    @DisplayName("userOwnsGame constructs correct URI with game ID")
    void userOwnsGame_constructsCorrectUri() {
        when(responseSpec.body(Boolean.class)).thenReturn(true);

        repository.userOwnsGame(GameId.from(GAME_ID), Jwt.withTokenValue(JWT).build());

        verify(requestHeadersUriSpec).uri("/" + GAME_ID);
    }

    @Test
    @DisplayName("userOwnsGame includes Bearer token in Authorization header")
    void userOwnsGame_includesBearerToken() {
        when(responseSpec.body(Boolean.class)).thenReturn(true);

        repository.userOwnsGame(GameId.from(GAME_ID), Jwt.withTokenValue(JWT).build());

        verify(headerSpec).header("Authorization", "Bearer " + JWT);
    }
}