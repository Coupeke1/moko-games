package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayerTest {

    @Test
    void validPlayer_createsSuccessfully() {
        PlayerId playerId = PlayerId.create();
        Player p = new Player(playerId, "mathias", "mathias@email.com");

        assertThat(p.id()).isEqualTo(playerId);
        assertThat(p.username()).isEqualTo("mathias");
    }

    @Test
    void nullId_throws() {
        assertThatThrownBy(() -> new Player(null, "john", "john@email.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullUsername_throws() {
        assertThatThrownBy(() -> new Player(PlayerId.create(), null, "null@null.null"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void blankUsername_throws() {
        assertThatThrownBy(() -> new Player(PlayerId.create(), " ", "null@null.null"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
