package be.kdg.team22.sessionservice.domain.lobby;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LobbyPlayerTest {

    @Test
    void validPlayer_createsSuccessfully() {
        UUID id = UUID.randomUUID();
        LobbyPlayer p = new LobbyPlayer(id, "mathias");

        assertThat(p.id()).isEqualTo(id);
        assertThat(p.username()).isEqualTo("mathias");
    }

    @Test
    void nullId_throws() {
        assertThatThrownBy(() -> new LobbyPlayer(null, "john"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullUsername_throws() {
        assertThatThrownBy(() -> new LobbyPlayer(UUID.randomUUID(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void blankUsername_throws() {
        assertThatThrownBy(() -> new LobbyPlayer(UUID.randomUUID(), " "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
