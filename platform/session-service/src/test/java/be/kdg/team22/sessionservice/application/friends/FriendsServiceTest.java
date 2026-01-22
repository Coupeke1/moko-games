package be.kdg.team22.sessionservice.application.friends;

import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.infrastructure.friends.ExternalFriendsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FriendsServiceTest {
    private ExternalFriendsRepository client = mock(ExternalFriendsRepository.class);
    private FriendsService service = new FriendsService(client);

    private Jwt jwtWithToken(String token) {
        return Jwt.withTokenValue(token)
                .header("alg", "none")
                .claim("preferred_username", "user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    @Test
    void findAllFriends_returnsMappedPlayerIds() {
        String tokenValue = "TOKEN-123";
        Jwt jwt = jwtWithToken(tokenValue);

        UUID f1 = UUID.randomUUID();
        UUID f2 = UUID.randomUUID();

        when(client.getFriendIds(tokenValue)).thenReturn(List.of(f1, f2));

        List<PlayerId> result = service.findAllFriends(jwt);

        assertThat(result)
                .extracting(PlayerId::value)
                .containsExactly(f1, f2);

        verify(client).getFriendIds(tokenValue);
    }

    @Test
    void findAllFriends_emptyList_returnsEmpty() {
        String tokenValue = "TOKEN-EMPTY";
        Jwt jwt = jwtWithToken(tokenValue);

        when(client.getFriendIds(tokenValue)).thenReturn(List.of());

        List<PlayerId> result = service.findAllFriends(jwt);

        assertThat(result).isEmpty();
        verify(client).getFriendIds(tokenValue);
    }

    @Test
    void findAllFriends_propagatesExceptions() {
        String tokenValue = "TOKEN-ERR";
        Jwt jwt = jwtWithToken(tokenValue);

        when(client.getFriendIds(tokenValue))
                .thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> service.findAllFriends(jwt))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("boom");

        verify(client).getFriendIds(tokenValue);
    }
}