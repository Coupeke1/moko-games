package be.kdg.team22.sessionservice.application.player;

import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.exceptions.PlayerNotFoundException;
import be.kdg.team22.sessionservice.infrastructure.player.ExternalPlayersRepository;
import be.kdg.team22.sessionservice.infrastructure.player.PlayerResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PlayerServiceTest {
    private ExternalPlayersRepository repo = mock(ExternalPlayersRepository.class);
    private PlayerService service = new PlayerService(repo);

    private Jwt jwtFor() {
        return Jwt.withTokenValue("TOKEN-123")
                .header("alg", "none")
                .claim("sub", "123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    @Test
    void findPlayer_success() {
        // Arrange
        UUID id = UUID.randomUUID();
        PlayerId pid = PlayerId.from(id);

        PlayerResponse response = new PlayerResponse(id, "mathias");

        when(repo.getById(id, "TOKEN-123")).thenReturn(Optional.of(response));

        // Act
        Player out = service.findPlayer(pid, jwtFor());

        // Assert
        assertThat(out.id().value()).isEqualTo(id);
        assertThat(out.username().value()).isEqualTo("mathias");

        verify(repo).getById(id, "TOKEN-123");
    }

    @Test
    void findPlayer_notFound_throws() {
        UUID id = UUID.randomUUID();
        PlayerId pid = PlayerId.from(id);

        when(repo.getById(id, "TOKEN-123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findPlayer(pid, jwtFor()))
                .isInstanceOf(PlayerNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void findPlayer_passesCorrectTokenAndId() {
        UUID id = UUID.randomUUID();
        PlayerId pid = PlayerId.from(id);

        when(repo.getById(any(), any())).thenReturn(Optional.of(new PlayerResponse(id, "user")));

        Jwt token = jwtFor();

        service.findPlayer(pid, token);

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);

        verify(repo).getById(idCaptor.capture(), tokenCaptor.capture());

        assertThat(idCaptor.getValue()).isEqualTo(id);
        assertThat(tokenCaptor.getValue()).isEqualTo("TOKEN-123");
    }
}