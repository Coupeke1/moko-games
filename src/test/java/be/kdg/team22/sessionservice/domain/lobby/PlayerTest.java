package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayerTest {

    @Test
    void validPlayer_createsSuccessfully() {
        PlayerId playerId = PlayerId.create();
        Player p = new Player(playerId, new PlayerName("mathias"));

        assertThat(p.id()).isEqualTo(playerId);
        assertThat(p.username().toString()).isEqualTo("mathias");
        assertThat(p.ready()).isFalse(); // default = false
    }

    @Test
    void nullId_throws() {
        assertThatThrownBy(() -> new Player(null, new PlayerName("john")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    void nullUsername_throws() {
        assertThatThrownBy(() -> new Player(PlayerId.create(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username");
    }

    @Test
    void blankUsername_throws() {
        assertThatThrownBy(() -> new Player(PlayerId.create(), new PlayerName(" ")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void withReady_setsReadyTrue() {
        Player p = new Player(PlayerId.create(), new PlayerName("mathias"));

        Player readyPlayer = p.withReady(true);

        assertThat(readyPlayer.ready()).isTrue();
        assertThat(p.ready()).isFalse();
    }

    @Test
    void withReady_setsReadyFalse() {
        Player p = new Player(PlayerId.create(), new PlayerName("mathias"));

        Player readyPlayer = p.withReady(true);
        Player unreadyPlayer = readyPlayer.withReady(false);

        assertThat(unreadyPlayer.ready()).isFalse();
        assertThat(readyPlayer.ready()).isTrue();
    }

    @Test
    void withReady_preservesIdentityAndUsername() {
        PlayerId id = PlayerId.create();
        PlayerName username = new PlayerName("alice");
        Player p = new Player(id, username);

        Player updated = p.withReady(true);

        assertThat(updated.id()).isEqualTo(id);
        assertThat(updated.username()).isEqualTo(username);
    }
}